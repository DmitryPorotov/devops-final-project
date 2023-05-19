package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseNoHouse}

case class SubPhaseCalculateCombatOutcome(
                                           mainPhase: MainPhase = PhaseAction
                                         ) extends SubPhaseNoHouse (
  mainPhase
) {
  def getSubPhaseName: String = "calculateCombatOutcome"
}
