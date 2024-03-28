package fwc.game.actionPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.board.MilitaryUnit
import fwc.game.board.TileNumber
import fwc.game.houses.HouseType
import fwc.game.planningPhase.Order
import fwc.gameLoading.{HouseCard, TidesOfBattleCard}
import fwc.game.gameRules
import fwc.gameSaving.actions.ActionException
import ujson.Value

import scala.collection.mutable

//note: should I move the combat object into relevant phases?
case class Combat(
                   attackerTileNum: TileNumber,
                   attackerHouse: HouseType,
                   attackerArmy: Seq[MilitaryUnit],
                   attackerOrder: Order,
                   attackerCard: HouseCard,
                   attackerCardResolved: Boolean,
                   attackerTidesOfBattle: TidesOfBattleCard,
                   attackerSupport: Seq[TileNumber],
                   defenderTileNum: TileNumber,
                   defenderHouse: HouseType,
                   defenderArmy: Seq[MilitaryUnit],
                   defenderOrder: Order,
                   defenderCard: HouseCard,
                   defenderCardResolved: Boolean,
                   defenderTidesOfBattle: TidesOfBattleCard,
                   defenderSupport: Seq[TileNumber],
                   combatOutcome: CombatOutcome = null
                 ) extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "attackerTileNum" -> attackerTileNum,
    "attackerHouse" -> attackerHouse.toString,
    "attackerArmy" -> ujson.Value(attackerArmy.map(_.toJson)),
    "attackerOrder" -> attackerOrder.toJson,
    "attackerCard" -> (if attackerCard != null then attackerCard.code else ujson.Null),
    "attackerCardResolved" -> attackerCardResolved,
    "attackerTidesOfBattle" -> (if attackerTidesOfBattle != null then attackerTidesOfBattle.code else ujson.Null),
    "attackerSupport" -> ujson.Arr.from(attackerSupport),
    "defenderTileNum" -> defenderTileNum,
    "defenderHouse" -> defenderHouse.toString,
    "defenderArmy" -> ujson.Value(defenderArmy.map(_.toJson)),
    "defenderOrder" -> (if defenderOrder != null then defenderOrder.toJson else ujson.Null),
    "defenderCard" -> (if defenderCard != null then defenderCard.code else ujson.Null),
    "defenderCardResolved" -> defenderCardResolved,
    "defenderTidesOfBattle" -> (if defenderTidesOfBattle != null then defenderTidesOfBattle.code else ujson.Null),
    "defenderSupport" -> ujson.Arr.from(defenderSupport),
    "combatOutcome" -> (if combatOutcome != null then combatOutcome.toJson else ujson.Null)
  )

  def addHouseCard(houseCard: HouseCard): Combat = {
    def throw_ : Nothing = throw new ActionException(s"House ${houseCard.house} has chosen a card already")
    
    if attackerHouse == houseCard.house
    then
      if attackerCard == null
      then copy(attackerCard = houseCard)
      else throw_
    else 
      if defenderHouse == houseCard.house
        then 
          if defenderCard == null
          then copy(defenderCard = houseCard)
          else throw_
      else throw new ActionException(s"House ${houseCard.house} is not participating in combat")
  }
  
  def winner: Option[HouseType] =
    if combatOutcome != null
    then combatOutcome.winner
    else None
    
  def loser: Option[HouseType] =
    if combatOutcome != null
    then 
      if combatOutcome.winner.nonEmpty
      then 
        if combatOutcome.winner.head == attackerHouse
        then Some(defenderHouse)
        else Some(attackerHouse)
      else None
    else None
    
  def winnerCard: Option[HouseCard] =
    if winner.isEmpty
    then None
    else 
      if winner.head == attackerHouse
      then Option(attackerCard)
      else Option(defenderCard)
      
  def loserCard: Option[HouseCard] =
    if winner.isEmpty
    then None
    else 
      if winner.head == attackerHouse
      then Option(defenderCard)
      else Option(attackerCard)
}

object Combat extends JsonParsable {
  override def fromJson(json: Value): Combat =
    if json == ujson.Null
    then return null
    val attackerHouse = HouseType.fromString(json("attackerHouse").str)
    val defenderHouse = HouseType.fromString(json("defenderHouse").str)
    Combat(
      json("attackerTileNum").num.toInt,
      attackerHouse,
      json("attackerArmy").arr.map(mu => MilitaryUnit.fromJson(mu)).toSeq,
      Order.fromJson(json("attackerOrder")),
      gameRules.houseCards.find(c =>
        c.code == json("attackerCard").num.toInt
        && c.house == attackerHouse
      ).head,
      json("attackerCardResolved").bool,
      gameRules.boardCards.tidesOfBattle.find(_.code == json("attackerTidesOfBattle").num.toInt).head,
      json("attackerSupport").arr.map(_.num.toInt).toSeq,
      json("defenderTileNum").num.toInt,
      defenderHouse,
      json("defenderArmy").arr.map(mu => MilitaryUnit.fromJson(mu)).toSeq,
      if json("defenderOrder") != ujson.Null then Order.fromJson(json("defenderOrder")) else null,
      gameRules.houseCards.find(c =>
        c.code == json("defenderCard").num.toInt
          && c.house == defenderHouse
      ).head,
      json("defenderCardResolved").bool,
      gameRules.boardCards.tidesOfBattle.find(_.code == json("defenderTidesOfBattle").num.toInt).head,
      json("defenderSupport").arr.map(_.num.toInt).toSeq,
      if json("combatOutcome") != ujson.Null then CombatOutcome.fromJson(json("combatOutcome")) else null,
    )
}
