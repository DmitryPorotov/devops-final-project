package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseRavenChangeOrder(
                                     houseType: HouseType,
                                     mainPhase: MainPhase = MainPhase.Action //note why action though?
                                   ) extends SubPhaseSingleHouse(houseType, mainPhase) {
  override def getSubPhaseName: String = "ravenChangeOrder"
}
