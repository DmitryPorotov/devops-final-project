package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseMultipleHouses}
import ujson.Value

case class SubPhaseResolveTiesAfterBiddingOnWildlings(
                                                       houseTypes: Seq[HouseType],
                                                       isWinner: Boolean,
                                                       mainPhase: MainPhase = PhaseRoundEvents
                                                     ) extends SubPhase(mainPhase) with SubPhaseMultipleHouses(
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "resolveTiesAfterBiddingOnWildlings"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("isWinner" -> isWinner)
    json
}
