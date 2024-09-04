package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseMultipleHouses}

case class SubPhaseChooseHouseCard(
                                  houseTypes: Seq[HouseType],
                                override val mainPhase: MainPhase = MainPhase.Action
                                 )extends SubPhase(mainPhase) with SubPhaseMultipleHouses (
   houseTypes,
   mainPhase
) {
  def getSubPhaseName: String = "chooseHouseCard"
}
