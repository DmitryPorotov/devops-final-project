package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseSingleHouse}

case class SubPhaseKillUnitsAfterBattle(
                                         override val houseType: HouseType,
                                         override val mainPhase: MainPhase = MainPhase.Action
                                       ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
    houseType, mainPhase
  ) {
  def getSubPhaseName: String = "killUnitsAfterBattle"
}
