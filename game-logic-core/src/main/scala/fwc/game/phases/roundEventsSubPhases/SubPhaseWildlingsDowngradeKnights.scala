package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseNoHouse, SubPhaseWildlingsMultiHousesMap}
import ujson.Value

case class SubPhaseWildlingsDowngradeKnights(
                                              houseTypes: Map[HouseType, Int],
                                              mainPhase: MainPhase = PhaseRoundEvents
                                            )
  extends SubPhaseWildlingsMultiHousesMap(houseTypes)
    with SubPhaseNoHouse(mainPhase) {

  override def getSubPhaseName: String = "wildlingsDowngradeKnights"
  
}
