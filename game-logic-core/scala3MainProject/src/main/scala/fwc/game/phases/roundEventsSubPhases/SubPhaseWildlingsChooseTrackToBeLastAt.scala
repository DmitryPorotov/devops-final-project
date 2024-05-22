package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseMultipleHouses}

case class SubPhaseWildlingsChooseTrackToBeLastAt(
                                                   houseTypes: Seq[HouseType],
                                                   mainPhase: MainPhase = PhaseRoundEvents
                                                 )
  extends SubPhase(mainPhase) with SubPhaseMultipleHouses(houseTypes, mainPhase) {

  override def getSubPhaseName: String = "wildlingsChooseTrackToBeLastAt"

}
