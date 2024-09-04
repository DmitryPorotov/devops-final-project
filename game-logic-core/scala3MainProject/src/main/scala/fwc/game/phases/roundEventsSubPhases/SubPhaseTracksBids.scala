package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseTracksBids(
                               houseTypes: Seq[HouseType],
                               trackType: TrackType,
                             override val mainPhase: MainPhase = MainPhase.RoundEvents
                             )
  extends SubPhase(mainPhase) with SubPhaseMultipleHouses(
    houseTypes, mainPhase
  )
    with SubPhaseMultipleHousesTracks(
    trackType
  ) {
  def getSubPhaseName: String = "tracksBids"
}
