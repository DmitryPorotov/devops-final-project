package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseChooseDisableMarchPlus1OrDefendOrders(
                                                          houseType: HouseType,
                                                          mainPhase: MainPhase = MainPhase.RoundEvents
                                                        ) 
  extends SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "chooseDisableMarchPlus1OrDefendOrders"
}
