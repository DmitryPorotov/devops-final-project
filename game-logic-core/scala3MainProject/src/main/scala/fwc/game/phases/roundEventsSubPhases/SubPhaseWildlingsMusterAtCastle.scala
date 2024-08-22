package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseWildlingsMusterAtCastle(
                                            override val houseType: HouseType,
                                            override val mainPhase: MainPhase = MainPhase.RoundEvents
                                          )
  extends SubPhase(mainPhase) with SubPhaseSingleHouse(houseType, mainPhase) {

  override def getSubPhaseName: String = "wildlingsMusterAtCastle"

}
