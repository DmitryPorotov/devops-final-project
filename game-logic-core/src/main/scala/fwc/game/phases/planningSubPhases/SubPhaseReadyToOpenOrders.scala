package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhasePlanning, SubPhaseMultipleHouses}

case class SubPhaseReadyToOpenOrders(
                                      houseTypes: Seq[HouseType],
                                      mainPhase: MainPhase = PhasePlanning
                                    ) extends SubPhaseMultipleHouses (
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "readyToOpenOrders"
}
