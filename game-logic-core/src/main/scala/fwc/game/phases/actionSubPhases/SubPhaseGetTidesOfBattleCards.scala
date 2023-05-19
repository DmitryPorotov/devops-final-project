package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseNoHouse, SubPhaseRandom}

case class SubPhaseGetTidesOfBattleCards(
                                          mainPhase: MainPhase = PhaseAction
                                        )
  extends SubPhaseNoHouse(mainPhase) with SubPhaseRandom {
  def getSubPhaseName: String = "getTidesOfBattleCards"

}
