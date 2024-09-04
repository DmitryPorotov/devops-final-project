package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.*
import fwc.gameLoading.RoundEventCard
import ujson.Value

case class SubPhaseSetEventCards(
                                houseTypes: Seq[HouseType],
                                  card1: Option[RoundEventCard] = None,
                                  card2: Option[RoundEventCard] = None,
                                  card3: Option[RoundEventCard] = None,
                                override val mainPhase: MainPhase = MainPhase.RoundEvents
                                )extends SubPhase(mainPhase) 
  with SubPhasePassive (mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase) {
  def getSubPhaseName: String = "setEventCards"

  override def toJson: Value =
    val json = super.toJson
    if card1.nonEmpty
    then json.obj.addOne("card1" -> card1.head.code)
    if card2.nonEmpty
    then json.obj.addOne("card2" -> card2.head.code)
    if card3.nonEmpty
    then json.obj.addOne("card3" -> card3.head.code)
    json
}
