package fwc.game.phases.roundEventsSubPhases

import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseMultipleHouses, SubPhasePassive}

case class SubPhaseOpenTrackBids(
                                  houseTypes: Seq[HouseType],
                                  trackType: TrackType,
                                override val mainPhase: MainPhase = MainPhase.RoundEvents
                                ) 
  extends SubPhase(mainPhase)
    with SubPhasePassive(mainPhase)
    with SubPhaseMultipleHouses(houseTypes, mainPhase) {

  override def getSubPhaseName: String = "openTrackBids"
}
