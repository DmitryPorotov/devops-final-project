package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseNoHouse, SubPhaseWildlingsMultiHousesMap}
import ujson.Value

case class SubPhaseWildlingsDowngradeKnights(
                                              houseTypes: Map[HouseType, Int],
                                              mainPhase: MainPhase = PhaseRoundEvents
                                            )
  extends SubPhase(mainPhase) with SubPhaseWildlingsMultiHousesMap(houseTypes)
    with SubPhaseNoHouse(mainPhase) {

  override def getSubPhaseName: String = "wildlingsDowngradeKnights"
  
}
