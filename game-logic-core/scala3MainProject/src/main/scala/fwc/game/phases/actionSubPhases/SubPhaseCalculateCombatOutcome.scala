package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse}

case class SubPhaseCalculateCombatOutcome(
                                           mainPhase: MainPhase = PhaseAction
                                         )extends SubPhase(mainPhase) with SubPhaseNoHouse (
  mainPhase
) {
  def getSubPhaseName: String = "calculateCombatOutcome"
}
