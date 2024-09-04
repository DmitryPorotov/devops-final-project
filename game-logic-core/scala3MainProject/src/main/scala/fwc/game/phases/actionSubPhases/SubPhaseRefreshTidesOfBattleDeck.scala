package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhasePassive, SubPhasePassiveMultipleHouses}

case class SubPhaseRefreshTidesOfBattleDeck(
                                           houseTypes: Seq[HouseType] = HouseType.getSeqOfAll,
                                           mainPhase: MainPhase = MainPhase.RoundEvents
                                           )
  extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
    with SubPhasePassive {
    def getSubPhaseName: String = "refreshTidesOfBattleDeck"
}
