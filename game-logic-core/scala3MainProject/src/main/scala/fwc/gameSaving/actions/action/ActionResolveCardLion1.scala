package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.Combat
import fwc.game.board.TileNumber
import fwc.game.houses.{HouseMoose, HouseType}
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import fwc.game.planningPhase.OrderMarch
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardLion1(
                                   gameState: GameState,
                                   houseType: HouseType,
                                   tileNumber: TileNumber
                                 )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (_, updatedCombat: Combat) = CardResolve.validateAndGetCombat(gameState.subPhase, houseType, gameState.combat)

    if !gameState.combat.winnerCard.exists(_.isLion1)
    then throw new ActionException("This phase is for lion 1")
    

    val updatedOrders =
      if tileNumber < 0
      then gameState.placedOrders
      else if gameState.placedOrders.getOrderByTileNumber(tileNumber).exists(_._1 == updatedCombat.loser.head)
      then gameState.placedOrders.removeOrder(updatedCombat.loser.head, tileNumber)
      else throw new ActionException(s"There is no order from house ${updatedCombat.loser.head} on this tile")

    val newPhase =
      if updatedCombat.loserCard.exists(_.isMoose3)
      then SubPhaseResolveHouseCard(HouseMoose, 3)
      else NextOrderFinder.nextSubPhase(gameState, OrderMarch, updatedCombat.attackerHouse)
    
    gameState.copy(
      subPhase = newPhase,
      placedOrders = updatedOrders,
      combat = if newPhase.isInstanceOf[SubPhaseResolveHouseCard] then gameState.combat else null,
      discardedHouseCards =
        if newPhase.isInstanceOf[SubPhaseResolveHouseCard]
        then gameState.discardedHouseCards
        else gameState.discardedHouseCards.resetDecksAfterCombat(updatedCombat)
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardLion1",
    "houseType" -> houseType.toString,
    "tileNumber" -> tileNumber
  )
}

object ActionResolveCardLion1 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardLion1 =
    ActionResolveCardLion1(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt
    )
}
