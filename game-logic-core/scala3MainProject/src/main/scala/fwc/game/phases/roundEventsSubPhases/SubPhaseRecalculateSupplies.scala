package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseNoHouse}

case class SubPhaseRecalculateSupplies(
                                        mainPhase: MainPhase = PhaseRoundEvents
                                      )extends SubPhase(mainPhase) with SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "recalculateSupplies"
}
