package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseWildlingsDiscardHouseCard(
                                              houseTypes: Seq[HouseType],
                                              mainPhase: MainPhase = MainPhase.RoundEvents
                                            )
  extends SubPhase(mainPhase) with SubPhaseMultipleHouses(
    houseTypes, mainPhase
  )
  {

  def getSubPhaseName: String = "wildlingsDiscardHouseCard"
}