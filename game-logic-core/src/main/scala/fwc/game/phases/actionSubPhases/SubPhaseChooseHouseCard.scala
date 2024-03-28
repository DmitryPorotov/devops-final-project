package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseMultipleHouses}

case class SubPhaseChooseHouseCard(
                                  houseTypes: Seq[HouseType],
                                  mainPhase: MainPhase = PhaseAction
                                 )extends SubPhase(mainPhase) with SubPhaseMultipleHouses (
   houseTypes,
   mainPhase
) {
  def getSubPhaseName: String = "chooseHouseCard"
}
