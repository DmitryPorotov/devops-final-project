package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseWildlingsKillUnits(
                                       houseTypes: Map[HouseType, Int],
                                       loserHouse: Option[HouseType] = None,
                                       mainPhase: MainPhase = MainPhase.RoundEvents
                                     )
  extends SubPhase(mainPhase)
  with SubPhaseWildlingsMultiHousesMap(houseTypes)
  {

  override def getSubPhaseName: String = "wildlingsKillUnits"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addAll(Map(
      "mainPhase" -> mainPhase.toString,
      "subPhase" -> getSubPhaseName,
    ))
    if loserHouse.nonEmpty
    then json.obj.addOne("loserHouse" -> loserHouse.head.toString)
    json

}
