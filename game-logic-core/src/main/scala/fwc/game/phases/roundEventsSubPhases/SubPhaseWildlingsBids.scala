package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseMultipleHouses}

case class SubPhaseWildlingsBids(
                                  houseTypes: Seq[HouseType],
                                  numberOfParticipants: Int,
                                  wildlingsStartedFrom12Points: Boolean,
                                  mainPhase: MainPhase = PhaseRoundEvents
                                ) extends SubPhaseMultipleHouses(
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "wildlingsBids"

  override def toJson: ujson.Value = {
    val json = super.toJson
    json.obj.addAll(Map(
      "numberOfParticipants" -> numberOfParticipants,
        "wildlingsStartedFrom12Points" -> wildlingsStartedFrom12Points
    ))
    json
  }
}
