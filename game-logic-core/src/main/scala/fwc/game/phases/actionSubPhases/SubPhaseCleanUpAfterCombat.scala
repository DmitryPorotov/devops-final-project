package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseNoHouse}

case class SubPhaseCleanUpAfterCombat(
                                       mainPhase: MainPhase = PhaseAction
                                     ) extends SubPhaseNoHouse (
  mainPhase
) {
  def getSubPhaseName: String = "cleanUpAfterCombat"
}
