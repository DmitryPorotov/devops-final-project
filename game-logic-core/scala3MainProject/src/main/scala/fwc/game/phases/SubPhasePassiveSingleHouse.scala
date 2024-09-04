package fwc.game.phases

import fwc.game.houses.HouseType

abstract class SubPhasePassiveSingleHouse(
                                           houseType: HouseType,
                                           mainPhase: MainPhase
                                         ) 
  extends SubPhaseSingleHouse(houseType, mainPhase) {}
