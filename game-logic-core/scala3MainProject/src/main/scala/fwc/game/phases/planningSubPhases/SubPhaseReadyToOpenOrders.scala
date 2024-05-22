package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhasePlanning, SubPhase, SubPhaseMultipleHouses}

//TODO unused now, remove later
case class SubPhaseReadyToOpenOrders(
                                      houseTypes: Seq[HouseType],
                                      mainPhase: MainPhase = PhasePlanning
                                    ) extends SubPhase(mainPhase) with SubPhaseMultipleHouses (
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "readyToOpenOrders"
}
