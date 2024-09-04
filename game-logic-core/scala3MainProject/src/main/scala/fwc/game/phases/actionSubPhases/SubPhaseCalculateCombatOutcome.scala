package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseCalculateCombatOutcome(
                                         houseTypes: Seq[HouseType],
                                         mainPhase: MainPhase = MainPhase.Action
                                         )
  extends SubPhasePassiveMultipleHouses (houseTypes,mainPhase)
    with SubPhasePassive {
    def getSubPhaseName: String = "calculateCombatOutcome"
}
