package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.Combat
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseAutoRetreatAfterBattle, SubPhaseKillUnitsAfterBattle, SubPhaseResolveHouseCard, SubPhaseRetreatUnitsAfterBattle}
import fwc.gameSaving.actions.{Action, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionAutoKillUnitsAfterBattle(
                                           gameState: GameState,
                                         ) extends Action(gameState) with JsonSerializable:
  override def doAction(): GameState = {
    val updatedCombat0 = removePreviouslyDefeatedUnits(gameState.combat)

    val updatedCombat1 = autoKillAttackerUnits(updatedCombat0)

    val updatedCombat2 = autoKillDefenderUnits(updatedCombat1)

    val isAttackerHigherOnThrone = updatedCombat2.attackerHouse.isHigherOnTrack(gameState.tracks(TrackType.Throne))(updatedCombat2.defenderHouse)

    if updatedCombat2.combatOutcome.attackerUnitsToKill > 0 && updatedCombat2.combatOutcome.defenderUnitsToKill > 0 then
      gameState.copy(
        subPhase = SubPhaseKillUnitsAfterBattle(
          if isAttackerHigherOnThrone then
            updatedCombat2.attackerHouse
          else
            updatedCombat2.defenderHouse
        ),
        combat = updatedCombat2
      )
    else if updatedCombat2.combatOutcome.attackerUnitsToKill > 0 then
      gameState.copy(subPhase = SubPhaseKillUnitsAfterBattle(updatedCombat2.attackerHouse), combat = updatedCombat2)
    else if updatedCombat2.combatOutcome.defenderUnitsToKill > 0 then
      gameState.copy(subPhase = SubPhaseKillUnitsAfterBattle(updatedCombat2.defenderHouse), combat = updatedCombat2)
    else
      gameState.copy(
        combat = updatedCombat2,
        subPhase =
          if updatedCombat2.winner.contains(updatedCombat2.defenderHouse)
          then SubPhaseAutoRetreatAfterBattle()
          else if updatedCombat2.winnerCard.exists(_.isWolf0) 
          then SubPhaseResolveHouseCard(HouseType.Wolf, 0)
            else SubPhaseRetreatUnitsAfterBattle(updatedCombat2.defenderHouse)
      )

  }

  private def removePreviouslyDefeatedUnits(combat: Combat): Combat =
    if combat.loser.contains(combat.defenderHouse) then
      combat.copy(
        defenderArmy = combat.defenderArmy.filter(!_.isDefeated)
      )
    else combat

  private def autoKillDefenderUnits(combat: Combat): Combat =
    if combat.combatOutcome.defenderUnitsToKill >= combat.defenderArmy.count(mu => mu.unitType.canBeMustered) then
      combat.copy(
        defenderArmy = Seq(),
        combatOutcome = combat.combatOutcome.copy(
          defenderUnitsToKill = 0
        )
      )
    else combat

  private def autoKillAttackerUnits(combat: Combat): Combat =
    if combat.combatOutcome.attackerUnitsToKill >= combat.attackerArmy.size then
      combat.copy(
        attackerArmy = Seq(),
        combatOutcome = combat.combatOutcome.copy(
          attackerUnitsToKill = 0
        )
      )
    else combat

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "autoKillUnitsAfterBattle",
  )

object ActionAutoKillUnitsAfterBattle extends JsonParsableAction {

  override def fromJson(gameState: GameState, json: Value): Action =
    ActionAutoKillUnitsAfterBattle(
      gameState
    )

}