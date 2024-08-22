package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhasePassive, SubPhaseSingleHouse}

case class SubPhaseResolveCardRose2(
                                     override val houseType: HouseType,
                                     override val mainPhase: MainPhase = MainPhase.Action
                                   )
  extends SubPhase(mainPhase)
  with SubPhasePassive (mainPhase)
  with SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "resolveCardRose2"
}
