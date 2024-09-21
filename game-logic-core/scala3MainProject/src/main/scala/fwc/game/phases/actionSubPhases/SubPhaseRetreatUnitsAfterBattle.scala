package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhaseSingleHouse}

case class SubPhaseRetreatUnitsAfterBattle(
                                            houseType: HouseType,
                                            mainPhase: MainPhase = MainPhase.Action
                                          ) 
  extends SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "retreatUnitsAfterBattle"
}
