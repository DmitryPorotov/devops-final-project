package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseAutoRetreatAfterBattle(
                                         houseType: HouseType,
                                           mainPhase: MainPhase = PhaseAction
                                         ) extends SubPhase(mainPhase) 
  with SubPhasePassive(mainPhase) 
  with SubPhaseSingleHouse(houseType, mainPhase)
  {
  def getSubPhaseName: String = "autoRetreatAfterBattle"
}
