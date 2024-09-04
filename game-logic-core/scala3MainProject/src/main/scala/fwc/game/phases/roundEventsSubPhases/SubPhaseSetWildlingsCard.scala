package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseSetWildlingsCard(
                                     houseTypes: Seq[HouseType],
                                     subPhaseWildlingsCard: SubPhaseWildlingsCard,
                                   override val mainPhase: MainPhase = MainPhase.RoundEvents
                                   )extends SubPhase(mainPhase)
  with SubPhasePassive (mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase) {
  override def toJson: Value = 
    val json = super.toJson
    json.obj.addOne(
      "subPhaseWildlingsCard" -> subPhaseWildlingsCard.toJson
    )
    json
  def getSubPhaseName: String = "setWildlingsCards"
}
