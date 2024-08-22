package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseWildlingsKillUnits(
                                       houseTypes: Map[HouseType, Int],
                                       loserHouse: Option[HouseType] = None,
                                       mainPhase: MainPhase = MainPhase.RoundEvents
                                     )
  extends SubPhase(mainPhase) with SubPhaseWildlingsMultiHousesMap(houseTypes)
  with SubPhasePassive(mainPhase) {

  override def getSubPhaseName: String = "wildlingsKillUnits"

  override def toJson: Value =
    val json = super.toJson
    if loserHouse.nonEmpty
    then json.obj.addOne("loserHouse" -> loserHouse.head.toString)
    json

}
