package fwc.game

import enrichment.ExtUPickleHashMap
import fwc.{JsonParsable, JsonSerializable}
import fwc.game.actionPhase.{Combat, DiscardedHouseCards, DominanceTokensUsage}
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.board.{Armies, TileNumber, Tracks}
import fwc.game.eventsPhase.{Bids, PowerTokens, Supplies, UsedMusteringPoints}
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.phases.SubPhase
import fwc.game.planningPhase.{AvailableOrders, Order, OrderType, PlacedOrders}
import ujson.Value

import scala.annotation.tailrec
import scala.util.Try

case class GameState(
                      subPhase: SubPhase,
                      armies: Armies,
                      placedOrders: PlacedOrders,
                      tracks: Tracks,
                      supplies: Supplies,
                      discardedHouseCards: DiscardedHouseCards,
                      powerTokens: PowerTokens,
                    //TODO need to add used board cards
                      boardCards: BoardCards,
                      dominanceTokensUsage: DominanceTokensUsage,
                      usedMusteringPoints: UsedMusteringPoints,
                      availableOrders: AvailableOrders,
                      bids: Bids,
                      combat: Combat = null,
                      wildlingCounter: Int = 6,
                      wildlingsStartedFrom12Points: Option[Boolean] = None,
                      roundCounter: Int = 1,
                      winner: Option[HouseType] = None
                    ) extends JsonSerializable {

  def toPersonalJson(myHouse: HouseType): ujson.Value = {
    val placedOrdersP = getPlacedOrders(myHouse)
    val combatP = getCombat(myHouse)
          
    ujson.Obj(
      toCleanJson.value.addPairs(
        GameStateParts.PlacedOrders.toString -> placedOrdersP.toJson,
        GameStateParts.Combat.toString -> (if combatP == null then ujson.Null else combatP.toJson),
      )
    )
  }


  private def getCombat(myHouse: HouseType) = {
    if combat != null then {
      if combat.attackerHouse == myHouse then
        combat.copy(
          defenderTidesOfBattle = null,
          defenderCard = null
        )
      else if combat.defenderHouse == myHouse then
        combat.copy(
          attackerCard = null,
          attackerTidesOfBattle = null
        )
      else
        combat.copy(
          defenderTidesOfBattle = null,
          defenderCard = null,
          attackerCard = null,
          attackerTidesOfBattle = null
        )
    }
    else null
  }

  private def getPlacedOrders(myHouse: HouseType) = {
    if subPhase.isInstanceOf[SubPhaseAddOrder] then
      placedOrders.copy(placedOrders = placedOrders.placedOrders.map(
        (houseType: HouseType, orders: Map[TileNumber, Order]) =>
          if houseType == myHouse then
            houseType -> orders
          else
            houseType -> orders.map(
              (tileNum: TileNumber, order: Order) =>
                tileNum -> Order(OrderType.ConsolidatePower)
            )
      ))
    else placedOrders
  }

  def toJsonForInputtingPlayer(houses: Seq[HouseType]): ujson.Obj =
    val placedOrdersP =
      if subPhase.isInstanceOf[SubPhaseAddOrder] then
        placedOrders.copy(placedOrders = placedOrders.placedOrders.map(
          (houseType: HouseType, orders: Map[TileNumber, Order]) =>
            if houses.contains(houseType) then
              houseType -> orders
            else
              houseType -> orders.map(
                (tileNum: TileNumber, order: Order) =>
                  tileNum -> Order(OrderType.ConsolidatePower)
              )
        ))
      else placedOrders
    val combatP =
      if combat != null then {
        if houses.contains(combat.attackerHouse) && houses.contains(combat.defenderHouse) then
          combat
        else if houses.contains(combat.defenderHouse) then
          combat.copy(
            attackerCard = null,
            attackerTidesOfBattle = null
          )
        else if houses.contains(combat.attackerHouse) then
          combat.copy(
            defenderCard = null,
            defenderTidesOfBattle = null
          )
        else
          combat.copy(
            defenderTidesOfBattle = null,
            defenderCard = null,
            attackerCard = null,
            attackerTidesOfBattle = null
          )
      }
      else null

    ujson.Obj(
      toCleanJson.value.addPairs(
        GameStateParts.PlacedOrders.toString -> placedOrdersP.toJson,
        GameStateParts.Combat.toString -> (if combatP == null then ujson.Null else combatP.toJson),
      )
    )
  
  def toJson: ujson.Value = {
    ujson.Obj(
      toCleanJson.value.addPairs(
        GameStateParts.BoardCards.toString -> boardCards.toJson,
        GameStateParts.PlacedOrders.toString -> placedOrders.toJson,
        GameStateParts.AvailableOrders.toString -> availableOrders.toJson,
        GameStateParts.Bids.toString -> bids.toJson,
        GameStateParts.Combat.toString -> (if combat == null then ujson.Null else combat.toJson)
      )
    )
  }

  def toCleanJson: ujson.Obj = {
    ujson.Obj(
      GameStateParts.SubPhase.toString -> subPhase.toJson,
      GameStateParts.Armies.toString -> armies.toJson,
      GameStateParts.Tracks.toString -> tracks.toJson,
      GameStateParts.Supplies.toString -> supplies.toJson,
      GameStateParts.DiscardedHouseCards.toString -> discardedHouseCards.toJson,
      GameStateParts.PowerTokens.toString -> powerTokens.toJson,
      GameStateParts.DominanceTokensUsage.toString -> dominanceTokensUsage.toJson,
      GameStateParts.UsedMusteringPoints.toString -> usedMusteringPoints.toJson,
      GameStateParts.WildlingCounter.toString -> wildlingCounter,
      GameStateParts.WildlingsStartedFrom12Points.toString ->
        (if wildlingsStartedFrom12Points.isEmpty then ujson.Null else wildlingsStartedFrom12Points.head),
      GameStateParts.RoundCounter.toString -> roundCounter,
    )
  }

  private def getPart(partName: String, userId: Int, houseType: Option[HouseType] = None): (String, ujson.Value) = {
    partName match
      case GameStateParts.SubPhase.string => GameStateParts.SubPhase.toString -> subPhase.toJson
      case GameStateParts.Armies.string => GameStateParts.Armies.toString -> armies.toJson
      case GameStateParts.Tracks.string => GameStateParts.Tracks.toString -> tracks.toJson
      case GameStateParts.Supplies.string => GameStateParts.Supplies.toString -> supplies.toJson
      case GameStateParts.PowerTokens.string => GameStateParts.PowerTokens.toString -> powerTokens.toJson
      case GameStateParts.DiscardedHouseCards.string => GameStateParts.DiscardedHouseCards.toString -> discardedHouseCards.toJson
      case GameStateParts.UsedMusteringPoints.string => GameStateParts.UsedMusteringPoints.toString -> usedMusteringPoints.toJson
      case GameStateParts.WildlingCounter.string => GameStateParts.WildlingCounter.toString -> wildlingCounter
      case GameStateParts.WildlingsStartedFrom12Points.string => GameStateParts.WildlingsStartedFrom12Points.string
        -> (if wildlingsStartedFrom12Points.isEmpty then ujson.Null else wildlingsStartedFrom12Points.head)
      case GameStateParts.RoundCounter.string => GameStateParts.RoundCounter.toString -> roundCounter
      case GameStateParts.AvailableOrders.string => GameStateParts.AvailableOrders.toString -> availableOrders.toJson
      case GameStateParts.PlacedOrders.string => GameStateParts.PlacedOrders.toString -> (if userId < 0 
        then placedOrders.toJson
        else
          getPlacedOrders(
          if subPhase.isInstanceOf[SubPhaseAddOrder]
          then 
            if houseType.nonEmpty 
            then houseType.head
            else throw new FWCException("No house type provided for placed orders in 'add order' phase.")
          else
            null
          ).toJson)
      case GameStateParts.Combat.string => GameStateParts.Combat.toString -> getCombat(
          if houseType.nonEmpty 
          then houseType.head
          else null
        ).toJson
  }

  def toPartialJson(parts: Seq[String], userId: Int, houseType: Option[HouseType] = None): ujson.Obj = {
    @tailrec
    def buildJson(parts: Seq[String], json: ujson.Obj = ujson.Obj()): ujson.Obj = {
      if parts.isEmpty
      then
        json
      else
        json.obj.addOne(getPart(parts.head, userId,  houseType))
        buildJson(parts.tail, json)
    }
    buildJson(parts)
  }
}

object GameState extends JsonParsable {
  override def fromJson(json: Value): GameState = {
    GameState(
      SubPhase.fromJson(json.obj(GameStateParts.SubPhase.string)),
      Armies.fromJson(json.obj(GameStateParts.Armies.string)),
      PlacedOrders.fromJson(json.obj(GameStateParts.PlacedOrders.string)),
      Tracks.fromJson(json.obj(GameStateParts.Tracks.string)),
      Supplies.fromJson(json.obj(GameStateParts.Supplies.string)),
      DiscardedHouseCards.fromJson(json.obj(GameStateParts.DiscardedHouseCards.string)),
      PowerTokens.fromJson(json.obj(GameStateParts.PowerTokens.string)),
      BoardCards.fromJson(json.obj(GameStateParts.BoardCards.string)),
      DominanceTokensUsage.fromJson(json.obj(GameStateParts.DominanceTokensUsage.string)),
      UsedMusteringPoints.fromJson(json.obj(GameStateParts.UsedMusteringPoints.string)),
      AvailableOrders.fromJson(json.obj(GameStateParts.AvailableOrders.string)),
      Bids.fromJson(json.obj(GameStateParts.Bids.string)),
      Combat.fromJson(json(GameStateParts.Combat.string)),
      json.obj(GameStateParts.WildlingCounter.string).num.toInt,
      Try[Option[Boolean]](json(GameStateParts.WildlingsStartedFrom12Points.string).boolOpt).getOrElse(None),
      json.obj(GameStateParts.RoundCounter.string).num.toInt
    )

  }
}