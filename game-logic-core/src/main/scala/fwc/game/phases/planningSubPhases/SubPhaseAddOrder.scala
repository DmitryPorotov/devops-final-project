package fwc.game.phases.planningSubPhases

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhasePlanning, SubPhaseMultipleHouses}

case class SubPhaseAddOrder(
                             houseTypes: Seq[HouseType],
                             mainPhase: MainPhase = PhasePlanning
                           ) extends SubPhaseMultipleHouses (
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "addOrder"
}
