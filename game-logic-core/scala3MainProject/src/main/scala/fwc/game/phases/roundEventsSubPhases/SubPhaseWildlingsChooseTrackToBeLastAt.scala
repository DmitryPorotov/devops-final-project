package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*

case class SubPhaseWildlingsChooseTrackToBeLastAt(
                                                 houseTypes: Seq[HouseType],
                                                 mainPhase: MainPhase = MainPhase.RoundEvents
                                                 )
  extends SubPhaseMultipleHouses(houseTypes, mainPhase) {

  override def getSubPhaseName: String = "wildlingsChooseTrackToBeLastAt"

}
