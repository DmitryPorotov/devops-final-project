package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseRavenGetWildlingsCard(
                                          override val houseType: HouseType,
                                          override val mainPhase: MainPhase = MainPhase.Action
                                        )
 extends SubPhase(mainPhase) 
   with SubPhasePassive(mainPhase)
   with SubPhaseSingleHouse(houseType, mainPhase)
   with SubPhaseRandom {
  def getSubPhaseName: String = "ravenGetWildlingsCard"

}
