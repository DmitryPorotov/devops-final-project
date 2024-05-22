package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}

case class SubPhaseRavenChangeOrder(
                                     houseType: HouseType,
                                     mainPhase: MainPhase = PhaseAction
                                   ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
  houseType, mainPhase
) {
  override def getSubPhaseName: String = "ravenChangeOrder"
}
