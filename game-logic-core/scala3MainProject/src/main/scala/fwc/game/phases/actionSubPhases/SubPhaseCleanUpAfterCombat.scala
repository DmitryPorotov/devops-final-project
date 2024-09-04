package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhasePassive, SubPhasePassiveMultipleHouses}

case class SubPhaseCleanUpAfterCombat(
                                     houseTypes: Seq[HouseType],
                                     mainPhase: MainPhase = MainPhase.Action
                                     )
  extends SubPhasePassiveMultipleHouses (houseTypes, mainPhase)
    with SubPhasePassive {
    def getSubPhaseName: String = "cleanUpAfterCombat"
}
