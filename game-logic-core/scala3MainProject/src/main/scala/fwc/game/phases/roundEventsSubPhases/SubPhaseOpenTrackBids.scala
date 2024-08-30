package fwc.game.phases.roundEventsSubPhases

import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhasePassive}

case class SubPhaseOpenTrackBids(
                                  trackType: TrackType,
                                  mainPhase: MainPhase = MainPhase.RoundEvents
                                ) extends SubPhase(mainPhase) with SubPhasePassive(mainPhase) {

  override def getSubPhaseName: String = "openTrackBids"
}
