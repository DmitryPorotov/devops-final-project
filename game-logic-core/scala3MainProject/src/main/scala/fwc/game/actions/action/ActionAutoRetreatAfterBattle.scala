package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.Combat
import fwc.game.actions.{Action, JsonParsableAction}
import fwc.game.phases.actionSubPhases.{SubPhaseCleanUpAfterCombat, SubPhaseRetreatUnitsAfterBattle}
import ujson.Value

case class ActionAutoRetreatAfterBattle(
                                         gameState: GameState,
                                       ) extends Action(gameState) with JsonSerializable:
  override def doAction(): GameState =
    val (updatedGameState1, attackerLost) = autoRetreatAttacker(gameState)
    if attackerLost then
      updatedGameState1
    else if updatedGameState1.combat.defenderArmy.count(_.unitType.canRetreat) > 0 then
      updatedGameState1.copy(
        subPhase = SubPhaseRetreatUnitsAfterBattle(updatedGameState1.combat.defenderHouse)
      )
    else
      updatedGameState1.copy(
        subPhase = SubPhaseCleanUpAfterCombat(Seq(updatedGameState1.combat.attackerHouse, updatedGameState1.combat.defenderHouse))
      )


  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "autoRetreatAfterBattle",
  )

  private def autoRetreatAttacker(gameState: GameState): (GameState, Boolean) =
    if gameState.combat.winner.contains(gameState.combat.defenderHouse) then
      (gameState.copy(
       armies = gameState.armies + (
         gameState.combat.attackerTileNum ->
           (gameState.armies.getOrElse(gameState.combat.attackerTileNum, Seq()) ++ gameState.combat.attackerArmy.filter(_.unitType.canRetreat))
         )
      ), true)
    else (gameState, false)

object ActionAutoRetreatAfterBattle  extends JsonParsableAction {

  override def fromJson(gameState: GameState, json: Value): Action =
    ActionAutoRetreatAfterBattle(
      gameState
    )

}
