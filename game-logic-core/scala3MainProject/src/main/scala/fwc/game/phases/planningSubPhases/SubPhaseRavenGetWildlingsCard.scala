package fwc.game.phases.planningSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse, SubPhaseRandom}

case class SubPhaseRavenGetWildlingsCard(
                                          mainPhase: MainPhase = PhaseAction
                                        )
 extends SubPhase(mainPhase) with SubPhaseNoHouse(mainPhase) with SubPhaseRandom {
  def getSubPhaseName: String = "ravenGetWildlingsCard"

}
