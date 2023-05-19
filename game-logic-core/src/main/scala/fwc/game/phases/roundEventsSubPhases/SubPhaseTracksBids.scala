package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseMultipleHouses, SubPhaseMultipleHousesTracks}

case class SubPhaseTracksBids(
                               houseTypes: Seq[HouseType],
                               trackType: TrackType,
                               mainPhase: MainPhase = PhaseRoundEvents
                             )
  extends SubPhaseMultipleHouses(
    houseTypes, mainPhase
  )
    with SubPhaseMultipleHousesTracks(
    trackType
  ) {
  def getSubPhaseName: String = "tracksBids"
}
