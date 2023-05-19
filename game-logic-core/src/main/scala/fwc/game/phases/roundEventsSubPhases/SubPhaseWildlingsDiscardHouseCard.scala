package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhaseMultipleHouses}

case class SubPhaseWildlingsDiscardHouseCard(
                                              houseTypes: Seq[HouseType],
                                              mainPhase: MainPhase = PhaseRoundEvents
                                            )
  extends SubPhaseMultipleHouses(
    houseTypes, mainPhase
  )
  {

  def getSubPhaseName: String = "wildlingsDiscardHouseCard"
}