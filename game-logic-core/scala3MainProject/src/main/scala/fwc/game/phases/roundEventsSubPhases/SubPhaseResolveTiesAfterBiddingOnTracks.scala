package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseResolveTiesAfterBiddingOnTracks(
                                                    override val houseType: HouseType,
                                                    trackType: TrackType,
                                                    override val mainPhase: MainPhase = MainPhase.RoundEvents
                                                  )
  extends SubPhase(mainPhase) with SubPhaseSingleHouse(houseType, mainPhase) {

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("trackType" -> trackType.toString)
    json

  def getSubPhaseName: String = "resolveTiesAfterBiddingOnTracks"
}
