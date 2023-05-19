package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseNoHouse, SubPhaseRandom}

case class SubPhaseGetEventCards(
                                  mainPhase: MainPhase = PhaseAction
                                )
  extends SubPhaseNoHouse(mainPhase) with SubPhaseRandom {
  def getSubPhaseName: String = "getEventCards"

}
