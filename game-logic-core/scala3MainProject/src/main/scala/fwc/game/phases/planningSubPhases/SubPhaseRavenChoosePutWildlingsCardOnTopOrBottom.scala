package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}

case class SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(
                                                             override val houseType: HouseType,
                                                             override val mainPhase: MainPhase = PhaseAction
                                                           ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
  houseType, mainPhase
) {
  def getSubPhaseName: String = "ravenChoosePutWildlingsCardOnTopOrBottom"
}
