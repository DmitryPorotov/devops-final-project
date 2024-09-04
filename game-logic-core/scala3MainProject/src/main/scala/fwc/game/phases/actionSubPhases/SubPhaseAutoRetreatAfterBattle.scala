package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseAutoRetreatAfterBattle(
                                          houseType: HouseType,
                                          mainPhase: MainPhase = MainPhase.Action
                                         )
  extends SubPhasePassiveSingleHouse(houseType, mainPhase)
    with SubPhasePassive
  {
  def getSubPhaseName: String = "autoRetreatAfterBattle"
}
