package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseNoHouse}

case class SubPhaseRefreshTidesOfBattleDeck(
                                             mainPhase: MainPhase = PhaseRoundEvents
                                           ) extends SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "refreshTidesOfBattleDeck"
}
