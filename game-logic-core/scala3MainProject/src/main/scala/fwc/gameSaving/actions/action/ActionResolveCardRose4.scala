package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.TileNumber
import fwc.game.houses.HouseType
import fwc.game.planningPhase.OrderType
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardRose4(
                                   gameState: GameState,
                                   houseType: HouseType,
                                   tileNumber: TileNumber
                                 ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat, updatedPhase) = CardResolveBeforeCombat.validateAndGetCombatAndCardPhase(
      gameState.subPhase,
      houseType,
      gameState.combat,
      gameState.powerTokens(HouseType.Kraken)
    )

    val tileToRemoveOrder = gameRules.board(tileNumber)
    val tileUnderAttack = gameRules.board(gameState.combat.defenderTileNum)
    if !tileToRemoveOrder.isNeighbourOf(tileUnderAttack)
    then throw new ActionException(s"${tileToRemoveOrder.name} ($tileNumber) is not a neighbour " +
      s"of ${tileUnderAttack.name} (${gameState.combat.defenderTileNum})")


    val houseOrderOpt = gameState.placedOrders.getOrderByTileNumber(tileNumber)
    val houseOrder =
      if houseOrderOpt.nonEmpty
      then houseOrderOpt.head
      else throw new ActionException(s"${tileToRemoveOrder.name} ($tileNumber) has no order")


    if isAttackerAction
    then
      if houseOrder._1 != gameState.combat.defenderHouse
      then throw new ActionException(s"The order in ${tileToRemoveOrder.name} ($tileNumber) " +
        s"is not your opponent's (${gameState.combat.defenderHouse}) order")
    else
      if houseOrder._1 != gameState.combat.attackerHouse
      then throw new ActionException(s"The order in ${tileToRemoveOrder.name} ($tileNumber) " +
        s"is not your opponent's (${gameState.combat.attackerHouse}) order")

    val updatedPlacedOrders = gameState.placedOrders.removeOrder(houseOrder._1, tileNumber)

    val updatedCombat2 =
      if houseOrder._2.orderType == OrderType.Support
      then
        if isAttackerAction
        then updatedCombat.copy(defenderSupport = updatedCombat.defenderSupport.filter(_ != tileNumber))
        else updatedCombat.copy(attackerSupport = updatedCombat.attackerSupport.filter(_ != tileNumber))
      else updatedCombat

    gameState.copy(
      subPhase = updatedPhase,
      combat = updatedCombat2,
      placedOrders = updatedPlacedOrders
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardRose4",
    "houseType" -> ujson.Str(houseType.toString),
    "tileNumber" -> ujson.Num(tileNumber)
  )
}

object ActionResolveCardRose4 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardRose4 =
    ActionResolveCardRose4(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt
    )
}