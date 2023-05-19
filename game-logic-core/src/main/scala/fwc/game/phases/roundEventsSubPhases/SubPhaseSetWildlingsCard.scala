package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseNoHouse}
import ujson.Value

case class SubPhaseSetWildlingsCard(
                                     subPhaseWildlingsCard: SubPhaseWildlingsCard,
                                     mainPhase: MainPhase = PhaseRoundEvents
                                   ) extends SubPhaseNoHouse (
    mainPhase
) {
  override def toJson: Value = 
    val json = super.toJson
    json.obj.addOne(
      "subPhaseWildlingsCard" -> subPhaseWildlingsCard.toJson
    )
    json
  def getSubPhaseName: String = "setWildlingsCards"
}
