package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseMultipleHouses, SubPhasePassive, SubPhasePassiveMultipleHouses}
import ujson.Value

case class SubPhaseCleanUpAfterCombat(
                                     houseTypes: Seq[HouseType],
                                     mainPhase: MainPhase = MainPhase.Action
                                     )
  extends SubPhasePassiveMultipleHouses (houseTypes, mainPhase)
    with SubPhasePassive {
    def getSubPhaseName: String = "cleanUpAfterCombat"
}
