package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseCleanUpAfterCombat, SubPhaseResolveConsolidatePowerOrder}
import fwc.game.planningPhase.{Order, OrderConsolidatePower}
import fwc.gameLoading.BoardTileSea
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction}
import ujson.Value

case class ActionResolveConsolidatePowerOrder(
                                               gameState: GameState
                                             ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
//    if !gameState.subPhase.isInstanceOf[SubPhaseResolveConsolidatePowerOrder]
//    then throw new ActionException("Wrong phase")

    val m = gameState.placedOrders.getTileNumToHouseTypeOrderMap

    val taxes = m.foldLeft(Map[HouseType, Int]())(
      (acc, tnToHouseOrder: (Int,(HouseType,Order))) =>
        val tile = gameRules.board(tnToHouseOrder._1)
        if tnToHouseOrder._2._2.orderType == OrderConsolidatePower 
          && tile.tileType != BoardTileSea
        then acc + (tnToHouseOrder._2._1 -> (acc.getOrElse(tnToHouseOrder._2._1, 0) + tile.powerPoints + 1))
        else acc
    )

    val updatedPowerTokens =
      gameState.powerTokens.copy(
        gameState.powerTokens.map(
          (ht, tk) =>
            ht -> (tk + taxes.getOrElse(ht, 0))
        )
      )

    gameState.copy(
      subPhase = SubPhaseCleanUpAfterCombat(),
      powerTokens = updatedPowerTokens
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveConsolidatePowerOrder"
  )
}

object ActionResolveConsolidatePowerOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveConsolidatePowerOrder =
    ActionResolveConsolidatePowerOrder(
      gameState
    )
}
