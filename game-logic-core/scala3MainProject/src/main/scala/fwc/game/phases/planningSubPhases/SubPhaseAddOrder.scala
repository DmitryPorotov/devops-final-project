package fwc.game.phases.planningSubPhases

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseAddOrder(
                             houseTypes: Seq[HouseType],
                             mainPhase: MainPhase = MainPhase.Planning
                           ) extends SubPhase(mainPhase) with SubPhaseMultipleHouses (
  houseTypes, mainPhase
) {
  def getSubPhaseName: String = "addOrder"
}
