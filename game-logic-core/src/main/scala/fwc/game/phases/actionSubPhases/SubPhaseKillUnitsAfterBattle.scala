package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}

case class SubPhaseKillUnitsAfterBattle(
                                         houseType: HouseType,
                                         mainPhase: MainPhase = PhaseAction
                                       ) extends SubPhaseSingleHouse(
    houseType, mainPhase
  ) {
  def getSubPhaseName: String = "killUnitsAfterBattle"
}
