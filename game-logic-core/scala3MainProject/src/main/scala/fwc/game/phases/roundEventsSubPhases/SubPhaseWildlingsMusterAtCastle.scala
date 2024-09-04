package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseWildlingsMusterAtCastle(
                                            houseType: HouseType,
                                            mainPhase: MainPhase = MainPhase.RoundEvents
                                          )
  extends SubPhaseSingleHouse(houseType, mainPhase) {

  override def getSubPhaseName: String = "wildlingsMusterAtCastle"

}
