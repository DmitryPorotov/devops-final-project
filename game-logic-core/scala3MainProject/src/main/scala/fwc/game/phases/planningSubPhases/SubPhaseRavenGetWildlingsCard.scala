package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhasePassive, SubPhaseRandom, SubPhaseSingleHouse}

case class SubPhaseRavenGetWildlingsCard(
                                          override val houseType: HouseType,
                                          override val mainPhase: MainPhase = PhaseAction
                                        )
 extends SubPhase(mainPhase) 
   with SubPhasePassive(mainPhase)
   with SubPhaseSingleHouse(houseType, mainPhase)
   with SubPhaseRandom {
  def getSubPhaseName: String = "ravenGetWildlingsCard"

}
