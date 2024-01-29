package fwc.game

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.actionPhase.{Combat, DiscardedHouseCards, DominanceTokensUsage}
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.board.{Armies, DominanceTokenType, TileNumber, Tracks}
import fwc.game.eventsPhase.{Bids, PowerTokens, Supplies, UsedMusteringPoints}
import fwc.game.houses.HouseType
import fwc.game.phases.{PhasePlanning, SubPhase}
import fwc.game.planningPhase.{AvailableOrders, Order, OrderConsolidatePower, OrderType, PlacedOrders}
import fwc.gameLoading.BoardTile
import ujson.Value

import scala.collection.mutable
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
    val placedOrdersP = 
      if subPhase.getMainPhase == PhasePlanning then
        placedOrders.copy(placedOrders = placedOrders.placedOrders.map(
        (houseType: HouseType, orders: Map[TileNumber, Order]) =>
          if houseType == myHouse then
            houseType -> orders
          else
            houseType -> orders.map(
              (tileNum: TileNumber, order: Order) =>
                tileNum -> Order(OrderConsolidatePower)
            )
        ))
      else placedOrders
    ujson.Obj(
      toCleanJson.value ++ mutable.LinkedHashMap[String, ujson.Value](
        "placedOrders" -> placedOrdersP.toJson,
      )
    )
  }

  def toJson: ujson.Value = {
    ujson.Obj(
      toCleanJson.value ++ mutable.LinkedHashMap[String, ujson.Value](
        "boardCards" -> boardCards.toJson,
        "placedOrders" -> placedOrders.toJson,
        "availableOrders" -> availableOrders.toJson,
        "bids" -> bids.toJson,
      )
    )
  }

  def toCleanJson: ujson.Obj = {
    ujson.Obj(
      "subPhase" -> subPhase.toJson,
      "armies" -> armies.toJson,
      "tracks" -> tracks.toJson,
      "supplies" -> supplies.toJson,
      "discardedHouseCards" -> discardedHouseCards.toJson,
      "powerTokens" -> powerTokens.toJson,
      "dominanceTokensUsage" -> dominanceTokensUsage.toJson,
      "usedMusteringPoints" -> usedMusteringPoints.toJson,
      "combat" -> (if combat == null then ujson.Null else combat.toJson),
      "wildlingCounter" -> wildlingCounter,
      "wildlingsStartedFrom12Points" ->
        (if wildlingsStartedFrom12Points.isEmpty then ujson.Null else wildlingsStartedFrom12Points.head),
      "roundCounter" -> roundCounter,
    )
  }

}

object GameState extends JsonParsable {
  override def fromJson(json: Value): GameState = {
    GameState(
      SubPhase.fromJson(json.obj("subPhase")),
      Armies.fromJson(json.obj("armies")),
      PlacedOrders.fromJson(json.obj("placedOrders")),
      Tracks.fromJson(json.obj("tracks")),
      Supplies.fromJson(json.obj("supplies")),
      DiscardedHouseCards.fromJson(json.obj("discardedHouseCards")),
      PowerTokens.fromJson(json.obj("powerTokens")),
      BoardCards.fromJson(json.obj("boardCards")),
      DominanceTokensUsage.fromJson(json.obj("dominanceTokensUsage")),
      UsedMusteringPoints.fromJson(json.obj("usedMusteringPoints")),
      AvailableOrders.fromJson(json.obj("availableOrders")),
      Bids.fromJson(json.obj("bids")),
      Combat.fromJson(json("combat")),
      json.obj("wildlingCounter").num.toInt,
      Try[Option[Boolean]](json("wildlingsStartedFrom12Points").boolOpt).getOrElse(None),
      json.obj("roundCounter").num.toInt
    )

  }
}