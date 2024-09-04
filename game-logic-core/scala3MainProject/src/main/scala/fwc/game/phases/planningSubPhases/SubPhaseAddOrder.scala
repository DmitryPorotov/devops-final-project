package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseAddOrder(
                             houseTypes: Seq[HouseType],
                             mainPhase: MainPhase = MainPhase.Planning
                           ) 
  extends SubPhaseMultipleHouses (houseTypes, mainPhase) {
  def getSubPhaseName: String = "addOrder"
}
