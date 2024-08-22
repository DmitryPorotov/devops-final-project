package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseAutoRetreatAfterBattle(
                                         override val houseType: HouseType,
                                         override val mainPhase: MainPhase = MainPhase.Action
                                         ) extends SubPhase(mainPhase) 
  with SubPhasePassive(mainPhase) 
  with SubPhaseSingleHouse(houseType, mainPhase)
  {
  def getSubPhaseName: String = "autoRetreatAfterBattle"
}
