package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(
                                                             houseType: HouseType,
                                                             mainPhase: MainPhase = MainPhase.Planning
                                                           ) 
  extends SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "ravenChoosePutWildlingsCardOnTopOrBottom"
}
