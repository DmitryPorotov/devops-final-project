package fwc.game.phases.planningSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseNoHouse, SubPhaseRandom}

case class SubPhaseRavenGetWildlingsCard(
                                          mainPhase: MainPhase = PhaseAction
                                        )
  extends SubPhaseNoHouse(mainPhase) with SubPhaseRandom {
  def getSubPhaseName: String = "ravenGetWildlingsCard"

}
