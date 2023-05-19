package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseNoHouse}

case class SubPhaseRecalculateSupplies(
                                        mainPhase: MainPhase = PhaseRoundEvents
                                      ) extends SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "recalculateSupplies"
}
