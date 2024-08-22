package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseResolveTiesAfterBiddingOnWildlings(
                                                       houseTypes: Seq[HouseType],
                                                       isWinner: Boolean,
                                                       mainPhase: MainPhase = MainPhase.RoundEvents
                                                     ) extends SubPhase(mainPhase) with SubPhaseMultipleHouses(
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "resolveTiesAfterBiddingOnWildlings"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("isWinner" -> isWinner)
    json
}
