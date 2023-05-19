package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseSingleHouse}

case class SubPhaseWildlingsMusterAtCastle(
                                            houseType: HouseType,
                                            mainPhase: MainPhase = PhaseRoundEvents
                                          )
  extends SubPhaseSingleHouse(houseType, mainPhase) {

  override def getSubPhaseName: String = "wildlingsMusterAtCastle"

}
