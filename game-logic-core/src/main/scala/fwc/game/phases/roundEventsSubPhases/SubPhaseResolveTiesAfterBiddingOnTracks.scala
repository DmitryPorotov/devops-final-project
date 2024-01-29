package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseMultipleHouses, SubPhaseMultipleHousesTracks, SubPhaseSingleHouse}
import ujson.Value

case class SubPhaseResolveTiesAfterBiddingOnTracks(
                                                    houseType: HouseType,
                                                    trackType: TrackType,
                                                    mainPhase: MainPhase = PhaseRoundEvents
                                                  )
  extends SubPhase(mainPhase) with SubPhaseSingleHouse(houseType, mainPhase) {

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("trackType" -> trackType.toString)
    json

  def getSubPhaseName: String = "resolveTiesAfterBiddingOnTracks"
}
