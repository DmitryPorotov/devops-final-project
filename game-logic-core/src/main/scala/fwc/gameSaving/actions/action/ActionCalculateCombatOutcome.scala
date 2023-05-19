package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.actionPhase.CombatOutcomeCalculator
import fwc.game.board.TrackThrone
import fwc.game.houses.{HouseType, HouseWolf}
import fwc.game.phases.actionSubPhases.{SubPhaseCalculateCombatOutcome, SubPhaseKillUnitsAfterBattle, SubPhaseRetreatUnitsAfterBattle}
import fwc.gameLoading.HouseCard
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction}
import ujson.Value

case class ActionCalculateCombatOutcome(
                                         gameState: GameState
                                       ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
//    if !gameState.subPhase.isInstanceOf[SubPhaseCalculateCombatOutcome]
//    then throw new ActionException("Wrong phase")
    
    val outcome = new CombatOutcomeCalculator(gameState).calculate()

    val newPhase = 
      if outcome.attackerUnitsToKill > 0 && outcome.defenderUnitsToKill == 0
      then SubPhaseKillUnitsAfterBattle(gameState.combat.attackerHouse)
      else 
        if outcome.attackerUnitsToKill == 0 && outcome.defenderUnitsToKill > 0
        then SubPhaseKillUnitsAfterBattle(gameState.combat.defenderHouse)
        else 
          if outcome.attackerUnitsToKill > 0 && outcome.defenderUnitsToKill > 0
          then 
            val throneTrack: Seq[HouseType] = gameState.tracks(TrackThrone)
            if throneTrack.indexOf(gameState.combat.attackerHouse) < throneTrack.indexOf(gameState.combat.defenderHouse)
            then SubPhaseKillUnitsAfterBattle(gameState.combat.attackerHouse)
            else SubPhaseKillUnitsAfterBattle(gameState.combat.defenderHouse)
          else SubPhaseRetreatUnitsAfterBattle(
            if gameState.combat.attackerHouse == outcome.winner 
            then gameState.combat.defenderHouse
            else gameState.combat.attackerHouse
          )
    gameState.copy(
      subPhase = newPhase,
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
