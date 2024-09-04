package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseGetWildlingsCard(
                                    houseTypes: Seq[HouseType],
                                     subPhaseWildlingsCard: SubPhaseWildlingsCard,
                                   mainPhase: MainPhase = MainPhase.RoundEvents
                                   )
 extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
   with SubPhaseRandom
   with SubPhasePassive {
  def getSubPhaseName: String = "getWildlingsCard"

  override def toJson: ujson.Value =
    val json = super.toJson
    json.obj.addOne(
      "subPhaseWildlingsCard" -> subPhaseWildlingsCard.toJson
    )
    json
}
