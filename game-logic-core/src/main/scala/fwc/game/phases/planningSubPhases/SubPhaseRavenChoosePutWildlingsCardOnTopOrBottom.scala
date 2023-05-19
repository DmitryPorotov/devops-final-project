package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseSingleHouse}

case class SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(
                                                             houseType: HouseType,
                                                             mainPhase: MainPhase = PhaseAction
                                                           ) extends SubPhaseSingleHouse(
  houseType, mainPhase
) {
  def getSubPhaseName: String = "ravenChoosePutWildlingsCardOnTopOrBottom"
}
