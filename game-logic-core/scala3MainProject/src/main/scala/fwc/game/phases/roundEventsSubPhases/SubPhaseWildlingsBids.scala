package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseWildlingsBids(
                                  houseTypes: Seq[HouseType],
                                  numberOfParticipants: Int,
                                  wildlingsStartedFrom12Points: Boolean,
                                  wildlingsCounter: Int,
                                override val mainPhase: MainPhase = MainPhase.RoundEvents,
                                ) extends SubPhase(mainPhase) with SubPhaseMultipleHouses(
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "wildlingsBids"

  override def toJson: ujson.Value = {
    val json = super.toJson
    json.obj.addAll(Map(
      "numberOfParticipants" -> numberOfParticipants,
      "wildlingsStartedFrom12Points" -> wildlingsStartedFrom12Points,
      "wildlingsCounter" -> wildlingsCounter
    ))
    json
  }
}
