package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(
                                                                 houseType: HouseType,
                                                                 mainPhase: MainPhase = MainPhase.RoundEvents
                                                               ) 
  extends SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "wildlingsChooseKill2UnitsOr2PositionsOnTrack"
}
