package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(
                                                             override val houseType: HouseType,
                                                             override val mainPhase: MainPhase = MainPhase.Action
                                                           ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
  houseType, mainPhase
) {
  def getSubPhaseName: String = "ravenChoosePutWildlingsCardOnTopOrBottom"
}
