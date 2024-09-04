package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(
                                                                 override val houseType: HouseType,
                                                                 override val mainPhase: MainPhase = MainPhase.RoundEvents
                                                               ) extends SubPhase(mainPhase) 
  with SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "wildlingsChooseKill2UnitsOr2PositionsOnTrack"
}
