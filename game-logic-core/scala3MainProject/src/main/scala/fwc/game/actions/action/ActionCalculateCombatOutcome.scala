package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.CombatOutcomeCalculator
import fwc.game.actions.{Action, JsonParsableAction}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseAutoKillUnitsAfterBattle
import ujson.Value

case class ActionCalculateCombatOutcome(
                                         gameState: GameState
                                       ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {

    val outcome = new CombatOutcomeCalculator(gameState).calculate()
    
    gameState.copy(
      subPhase = SubPhaseAutoKillUnitsAfterBattle(Seq(gameState.combat.attackerHouse, gameState.combat.defenderHouse)),
      combat = gameState.combat.copy(
        combatOutcome = outcome
      )
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "calculateCombatOutcome"
  )
}

object ActionCalculateCombatOutcome extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionCalculateCombatOutcome =
    ActionCalculateCombatOutcome(
      gameState
    )
}
