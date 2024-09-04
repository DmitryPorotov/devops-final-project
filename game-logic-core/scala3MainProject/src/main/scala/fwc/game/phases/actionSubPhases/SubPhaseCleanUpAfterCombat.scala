package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseMultipleHouses, SubPhasePassive}
import ujson.Value

case class SubPhaseCleanUpAfterCombat(
                                       houseTypes: Seq[HouseType],
                                     override val mainPhase: MainPhase = MainPhase.Action
                                     )extends SubPhase(mainPhase)
  with SubPhasePassive (mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase)
  {
    override def toJson: Value = super.toJson
    def getSubPhaseName: String = "cleanUpAfterCombat"
}
