package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseSingleHouse}

case class SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(
                                                                 override val houseType: HouseType,
                                                                 override val mainPhase: MainPhase = PhaseRoundEvents
                                                               ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
  houseType: HouseType,
  mainPhase: MainPhase
) {
  def getSubPhaseName: String = "wildlingsChooseKill2UnitsOr2PositionsOnTrack"
}
