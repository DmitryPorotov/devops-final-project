package fwc.game.phases

import fwc.game.phases.MainPhase.Planning

case class SubPhaseAwaitingStart(mainPhase: MainPhase = Planning)
  extends SubPhase(mainPhase) 
    with SubPhasePassive(mainPhase):
  override def getSubPhaseName: String = "awaitingStart"
