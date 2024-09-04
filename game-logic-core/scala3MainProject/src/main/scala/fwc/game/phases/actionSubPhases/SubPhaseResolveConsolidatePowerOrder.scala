package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhasePassive, SubPhasePassiveMultipleHouses}

case class SubPhaseResolveConsolidatePowerOrder(
                                               houseTypes: Seq[HouseType] = HouseType.getSeqOfAll ,
                                               mainPhase: MainPhase = MainPhase.Action
                                               )
  extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
    with SubPhasePassive {
    def getSubPhaseName: String = "resolveConsolidatePowerOrder"
}

