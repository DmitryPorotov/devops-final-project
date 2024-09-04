package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

//TODO unused now, remove later
case class SubPhaseReadyToOpenOrders(
                                      houseTypes: Seq[HouseType],
                                      mainPhase: MainPhase = MainPhase.Planning
                                    )
  extends SubPhaseMultipleHouses (houseTypes, mainPhase) {
  def getSubPhaseName: String = "readyToOpenOrders"
}
