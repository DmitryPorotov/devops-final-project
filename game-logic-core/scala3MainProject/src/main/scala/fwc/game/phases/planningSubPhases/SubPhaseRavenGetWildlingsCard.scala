package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseRavenGetWildlingsCard(
                                          houseType: HouseType,
                                          mainPhase: MainPhase = MainPhase.Planning
                                        )
 extends SubPhasePassiveSingleHouse(houseType, mainPhase)
   with SubPhaseRandom
   with SubPhasePassive {
  def getSubPhaseName: String = "ravenGetWildlingsCard"

}
