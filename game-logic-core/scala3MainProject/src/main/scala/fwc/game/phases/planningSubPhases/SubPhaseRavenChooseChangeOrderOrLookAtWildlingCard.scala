package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard(
                                                               houseType: HouseType,
                                                               mainPhase: MainPhase = MainPhase.Action
                                                             ) extends SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "ravenChooseChangeOrderOrLookAtWildlingCard"
}
