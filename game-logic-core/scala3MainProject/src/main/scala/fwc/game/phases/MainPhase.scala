package fwc.game.phases

sealed trait MainPhase


object MainPhase {
  case object RoundEvents extends MainPhase {
    override def toString: String = "phaseRoundEvents"
  }

  case object Planning extends MainPhase {
    override def toString: String = "phasePlanning"
  }

  case object Action extends MainPhase {
    override def toString: String = "phaseAction"
  }
  def stringToMainPhase(str: String): MainPhase = str match
    case "phaseRoundEvents" => RoundEvents
    case "phasePlanning" => Planning
    case "phaseAction" => Action
}