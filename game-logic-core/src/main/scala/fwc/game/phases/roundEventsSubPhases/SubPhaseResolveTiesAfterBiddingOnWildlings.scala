package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseMultipleHouses}
import ujson.Value

case class SubPhaseResolveTiesAfterBiddingOnWildlings(
                                                       houseTypes: Seq[HouseType],
                                                       isWinner: Boolean,
                                                       mainPhase: MainPhase = PhaseRoundEvents
                                                     ) extends SubPhaseMultipleHouses(
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "resolveTiesAfterBiddingOnWildlings"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("isWinner" -> isWinner)
    json
}
