package fwc.game.phases.planningSubPhases

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhasePlanning, SubPhase, SubPhaseMultipleHouses}

case class SubPhaseAddOrder(
                             houseTypes: Seq[HouseType],
                             mainPhase: MainPhase = PhasePlanning
                           ) extends SubPhase(mainPhase) with SubPhaseMultipleHouses (
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "addOrder"
}
