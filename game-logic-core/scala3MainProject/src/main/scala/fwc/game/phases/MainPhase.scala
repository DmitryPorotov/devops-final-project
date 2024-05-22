package fwc.game.phases

sealed trait MainPhase

case object PhaseRoundEvents extends MainPhase {
  override def toString: String = "phaseRoundEvents"
}

case object PhasePlanning extends MainPhase {
  override def toString: String = "phasePlanning"
}

case object PhaseAction extends MainPhase {
  override def toString: String = "phaseAction"
}

object MainPhase {
  def stringToMainPhase(str: String): MainPhase = str match
    case "phaseRoundEvents" => PhaseRoundEvents
    case "phasePlanning" => PhasePlanning
    case "phaseAction" => PhaseAction
}