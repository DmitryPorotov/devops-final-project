package fwc.game.phases.planningSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

//TODO unused now, remove later
case class SubPhaseReadyToOpenOrders(
                                      houseTypes: Seq[HouseType],
                                    override val mainPhase: MainPhase = MainPhase.Planning
                                    ) extends SubPhase(mainPhase) with SubPhaseMultipleHouses (
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "readyToOpenOrders"
}
