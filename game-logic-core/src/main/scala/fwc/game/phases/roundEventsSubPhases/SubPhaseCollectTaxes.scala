package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseNoHouse}

case class SubPhaseCollectTaxes(
                                 mainPhase: MainPhase = PhaseRoundEvents
                               ) extends SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "collectTaxes"
}
