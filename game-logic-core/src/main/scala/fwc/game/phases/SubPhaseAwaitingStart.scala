package fwc.game.phases

case class SubPhaseAwaitingStart(mainPhase: MainPhase = PhasePlanning) extends SubPhaseNoHouse(mainPhase):
  override def getSubPhaseName: String = "awaitingStart"
