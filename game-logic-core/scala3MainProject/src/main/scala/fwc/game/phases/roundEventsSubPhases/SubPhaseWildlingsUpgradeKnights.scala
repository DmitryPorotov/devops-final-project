package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseSingleHouse}

case class SubPhaseWildlingsUpgradeKnights(
                                            houseType: HouseType,
                                            mainPhase: MainPhase = PhaseRoundEvents
                                          )
  extends SubPhase(mainPhase) with SubPhaseSingleHouse(houseType, mainPhase) {

  override def getSubPhaseName: String = "wildlingsUpgradeKnights"

}
