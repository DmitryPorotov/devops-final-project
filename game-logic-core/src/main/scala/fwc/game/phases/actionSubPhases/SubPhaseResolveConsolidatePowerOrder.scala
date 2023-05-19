package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseNoHouse}

case class SubPhaseResolveConsolidatePowerOrder(
                                                 mainPhase: MainPhase = PhaseRoundEvents
                                               ) extends SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "resolveConsolidatePowerOrder"
}

