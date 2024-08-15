package fwc.game.phases

case class SubPhaseAwaitingStart(mainPhase: MainPhase = PhasePlanning)
  extends SubPhase(mainPhase) 
    with SubPhasePassive(mainPhase):
  override def getSubPhaseName: String = "awaitingStart"
