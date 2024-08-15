package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseMultipleHouses, SubPhasePassive}
import ujson.Value

case class SubPhaseCleanUpAfterRound(
                                      houseTypes: Seq[HouseType],
                                      mainPhase: MainPhase = PhaseAction
                                    )extends SubPhase(mainPhase)
  with SubPhasePassive (mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase)
  {
    override def toJson: Value = super.toJson
    def getSubPhaseName: String = "cleanUpAfterRound"
}
