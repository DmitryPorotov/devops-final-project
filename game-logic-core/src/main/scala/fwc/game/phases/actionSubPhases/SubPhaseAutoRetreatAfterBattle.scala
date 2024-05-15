package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse}

case class SubPhaseAutoRetreatAfterBattle(
                                           mainPhase: MainPhase = PhaseAction
                                         ) extends SubPhase(mainPhase) with SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "autoRetreatAfterBattle"
}
