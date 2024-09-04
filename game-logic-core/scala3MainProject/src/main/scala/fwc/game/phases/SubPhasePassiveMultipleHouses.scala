package fwc.game.phases

import fwc.game.houses.HouseType

abstract class SubPhasePassiveMultipleHouses(
                                              houseTypes: Seq[HouseType],
                                              mainPhase: MainPhase
                                             )
  extends SubPhaseMultipleHouses(houseTypes, mainPhase) {
}
