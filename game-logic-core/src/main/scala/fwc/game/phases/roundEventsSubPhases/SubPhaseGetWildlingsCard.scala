package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse, SubPhaseRandom}

case class SubPhaseGetWildlingsCard(
                                     subPhaseWildlingsCard: SubPhaseWildlingsCard,
                                     mainPhase: MainPhase = PhaseAction
                                   )
 extends SubPhase(mainPhase) with SubPhaseNoHouse(mainPhase) with SubPhaseRandom {
  def getSubPhaseName: String = "getWildlingsCard"

  override def toJson: ujson.Value =
    val json = super.toJson
    json.obj.addOne(
      "subPhaseWildlingsCard" -> subPhaseWildlingsCard.toJson
    )
    json
}
