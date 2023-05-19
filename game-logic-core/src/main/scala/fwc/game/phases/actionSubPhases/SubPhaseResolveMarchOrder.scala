package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseSingleHouse}

case class SubPhaseResolveMarchOrder(
                                      houseType: HouseType,
                                      mainPhase: MainPhase = PhaseAction
                                    ) extends SubPhaseSingleHouse(
  houseType, mainPhase
) {
  def getSubPhaseName: String = "resolveMarchOrder"
}
