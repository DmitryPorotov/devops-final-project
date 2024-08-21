package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}

case class SubPhaseChooseToUseValyrianSteelBlade(
                                                  override val houseType: HouseType,
                                                  override val mainPhase: MainPhase = PhaseAction
                                                ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
    houseType, mainPhase
  ) {
  def getSubPhaseName: String = "chooseToUseValyrianSteelBlade"
}
