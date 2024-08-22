package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseWildlingsDowngradeKnights(
                                              houseTypes: Map[HouseType, Int],
                                              mainPhase: MainPhase = MainPhase.RoundEvents
                                            )
  extends SubPhase(mainPhase) 
    with SubPhaseWildlingsMultiHousesMap(houseTypes)
    with SubPhasePassive(mainPhase) {

  override def getSubPhaseName: String = "wildlingsDowngradeKnights"
  
}
