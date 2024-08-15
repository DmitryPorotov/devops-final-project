package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhasePassive, SubPhaseWildlingsMultiHousesMap}
import ujson.Value

case class SubPhaseWildlingsDowngradeKnights(
                                              houseTypes: Map[HouseType, Int],
                                              mainPhase: MainPhase = PhaseRoundEvents
                                            )
  extends SubPhase(mainPhase) 
    with SubPhaseWildlingsMultiHousesMap(houseTypes)
    with SubPhasePassive(mainPhase) {

  override def getSubPhaseName: String = "wildlingsDowngradeKnights"
  
}
