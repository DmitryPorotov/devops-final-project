package fwc.game.phases.actionSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse, SubPhaseRandom}

case class SubPhaseGetTidesOfBattleCards(
                                          mainPhase: MainPhase = PhaseAction
                                        )
 extends SubPhase(mainPhase) with SubPhaseNoHouse(mainPhase) with SubPhaseRandom {
  def getSubPhaseName: String = "getTidesOfBattleCards"

}
