package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseSingleHouse}

case class SubPhaseMuster(
                           houseType: HouseType,
                           mainPhase: MainPhase = PhaseRoundEvents
                         ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
    houseType: HouseType,
    mainPhase: MainPhase
) {
  def getSubPhaseName: String = "muster"
}
