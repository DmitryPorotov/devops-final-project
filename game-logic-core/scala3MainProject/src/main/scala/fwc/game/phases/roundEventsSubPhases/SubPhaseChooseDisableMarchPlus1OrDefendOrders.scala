package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseChooseDisableMarchPlus1OrDefendOrders(
                                                          override val houseType: HouseType,
                                                          override val mainPhase: MainPhase = MainPhase.RoundEvents
                                                        ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
    houseType, mainPhase
  ) {
  def getSubPhaseName: String = "chooseDisableMarchPlus1OrDefendOrders"
}
