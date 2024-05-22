package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseNoHouse, SubPhaseWildlingsMultiHousesMap}
import ujson.Value

case class SubPhaseWildlingsKillUnits(
                                       houseTypes: Map[HouseType, Int],
                                       loserHouse: Option[HouseType] = None,
                                       mainPhase: MainPhase = PhaseRoundEvents
                                     )
  extends SubPhase(mainPhase) with SubPhaseWildlingsMultiHousesMap(houseTypes)
  with SubPhaseNoHouse(mainPhase) {

  override def getSubPhaseName: String = "wildlingsKillUnits"

  override def toJson: Value =
    val json = super.toJson
    if loserHouse.nonEmpty
    then json.obj.addOne("loserHouse" -> loserHouse.head.toString)
    json

}
