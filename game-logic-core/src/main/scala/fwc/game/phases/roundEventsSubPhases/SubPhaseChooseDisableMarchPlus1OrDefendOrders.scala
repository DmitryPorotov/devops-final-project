package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseSingleHouse}

case class SubPhaseChooseDisableMarchPlus1OrDefendOrders(
                                                          houseType: HouseType,
                                                          mainPhase: MainPhase = PhaseRoundEvents
                                                        ) extends SubPhaseSingleHouse(
    houseType, mainPhase
  ) {
  def getSubPhaseName: String = "chooseDisableMarchPlus1OrDefendOrders"
}
