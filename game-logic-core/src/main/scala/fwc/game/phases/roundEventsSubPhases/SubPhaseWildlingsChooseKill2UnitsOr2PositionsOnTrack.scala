package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseSingleHouse}

case class SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(
                                                                 houseType: HouseType,
                                                                 mainPhase: MainPhase = PhaseRoundEvents
                                                               ) extends SubPhaseSingleHouse(
  houseType: HouseType,
  mainPhase: MainPhase
) {
  def getSubPhaseName: String = "wildlingsChooseKill2UnitsOr2PositionsOnTrack"
}
