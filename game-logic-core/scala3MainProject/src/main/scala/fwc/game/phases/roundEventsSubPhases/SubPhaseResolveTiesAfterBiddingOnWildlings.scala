package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

/**
 * 
 * @param houseType the throne owner
 * @param houseTypes contenders for the first or the last place
 * @param subPhaseGetWildlingsCard SubPhaseGetWildlingsCard 
 * @param mainPhase main phase
 */
case class SubPhaseResolveTiesAfterBiddingOnWildlings(
                                                       houseType: HouseType,
                                                       houseTypes: Seq[HouseType],
                                                       subPhaseGetWildlingsCard: SubPhaseGetWildlingsCard,
                                                       mainPhase: MainPhase = MainPhase.RoundEvents
                                                     ) 
  extends SubPhaseMultipleHouses(houseTypes, mainPhase) {
  def getSubPhaseName: String = "resolveTiesAfterBiddingOnWildlings"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("subPhaseGetWildlingsCard" -> subPhaseGetWildlingsCard.toJson)
    json.obj.addOne("houseType" -> houseType.toString)
    json
}
