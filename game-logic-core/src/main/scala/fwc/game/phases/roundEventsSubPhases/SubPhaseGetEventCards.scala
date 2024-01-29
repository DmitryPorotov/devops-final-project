package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse, SubPhaseRandom}

case class SubPhaseGetEventCards(
                                  mainPhase: MainPhase = PhaseAction
                                )
 extends SubPhase(mainPhase) with SubPhaseNoHouse(mainPhase) with SubPhaseRandom {
  def getSubPhaseName: String = "getEventCards"

}
