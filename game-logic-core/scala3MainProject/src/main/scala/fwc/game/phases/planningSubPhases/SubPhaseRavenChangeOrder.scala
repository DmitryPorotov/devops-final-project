package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseRavenChangeOrder(
                                     override val houseType: HouseType,
                                     override val mainPhase: MainPhase = MainPhase.Action //note why action though?
                                   ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
  houseType, mainPhase
) {
  override def getSubPhaseName: String = "ravenChangeOrder"
}
