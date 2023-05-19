package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseNoHouse}
import fwc.gameLoading.RoundEventCard
import ujson.Value

case class SubPhaseSetEventCards(
                                  card1: Option[RoundEventCard] = None,
                                  card2: Option[RoundEventCard] = None,
                                  card3: Option[RoundEventCard] = None,
                                  mainPhase: MainPhase = PhaseRoundEvents
                                ) extends SubPhaseNoHouse (
    mainPhase
  ) {
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
