package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseSingleHouse}

case class SubPhaseRetreatUnitsAfterBattle(
                                            houseType: HouseType,
                                            mainPhase: MainPhase = MainPhase.Action
                                          ) 
  extends SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "retreatUnitsAfterBattle"
}
