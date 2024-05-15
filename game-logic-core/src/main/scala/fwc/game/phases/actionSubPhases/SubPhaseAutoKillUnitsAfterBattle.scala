package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse}

case class SubPhaseAutoKillUnitsAfterBattle(
                                             mainPhase: MainPhase = PhaseAction
                                           ) extends SubPhase(mainPhase) with SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "autoKillUnitsAfterBattle"
}
