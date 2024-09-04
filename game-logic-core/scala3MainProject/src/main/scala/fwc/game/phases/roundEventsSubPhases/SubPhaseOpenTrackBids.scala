package fwc.game.phases.roundEventsSubPhases

import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseMultipleHouses, SubPhasePassive, SubPhasePassiveMultipleHouses}

case class SubPhaseOpenTrackBids(
                                  houseTypes: Seq[HouseType],
                                  trackType: TrackType,
                                 mainPhase: MainPhase = MainPhase.RoundEvents
                                ) 
  extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
    with SubPhasePassive {

  override def getSubPhaseName: String = "openTrackBids"
}
