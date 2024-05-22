package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseNoHouse}

case class SubPhaseRefreshTidesOfBattleDeck(
                                             mainPhase: MainPhase = PhaseRoundEvents
                                           )extends SubPhase(mainPhase) with SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "refreshTidesOfBattleDeck"
}
