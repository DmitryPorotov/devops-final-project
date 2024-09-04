package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseSetWildlingsCard(
                                    houseTypes: Seq[HouseType],
                                     subPhaseWildlingsCard: SubPhaseWildlingsCard,
                                    mainPhase: MainPhase = MainPhase.RoundEvents
                                   )
  extends SubPhasePassiveMultipleHouses (houseTypes, mainPhase)
    with SubPhasePassive {
  override def toJson: Value = 
    val json = super.toJson
    json.obj.addOne(
      "subPhaseWildlingsCard" -> subPhaseWildlingsCard.toJson
    )
    json
  def getSubPhaseName: String = "setWildlingsCards"
}
