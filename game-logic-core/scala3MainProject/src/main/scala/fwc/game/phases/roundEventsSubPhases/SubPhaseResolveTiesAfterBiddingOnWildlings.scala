package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

/**
 * 
 * @param houseType the throne owner
 * @param houseTypes contenders for the first or the last place
 * @param isWinner was the battle against wildlings won
 * @param mainPhase main phase
 */
case class SubPhaseResolveTiesAfterBiddingOnWildlings(
                                                       houseType: HouseType,
                                                       houseTypes: Seq[HouseType],
                                                       isWinner: Boolean,
                                                       mainPhase: MainPhase = MainPhase.RoundEvents
                                                     ) 
  extends SubPhaseMultipleHouses(houseTypes, mainPhase) {
  def getSubPhaseName: String = "resolveTiesAfterBiddingOnWildlings"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("isWinner" -> isWinner)
    json.obj.addOne("houseType" -> houseType.toString)
    json
}
