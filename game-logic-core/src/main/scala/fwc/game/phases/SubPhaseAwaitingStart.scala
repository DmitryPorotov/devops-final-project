package fwc.game.phases

case class SubPhaseAwaitingStart(mainPhase: MainPhase = PhasePlanning)extends SubPhase(mainPhase) with SubPhaseNoHouse(mainPhase):
  override def getSubPhaseName: String = "awaitingStart"
