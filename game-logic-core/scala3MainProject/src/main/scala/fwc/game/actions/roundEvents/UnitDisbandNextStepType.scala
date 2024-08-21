package fwc.game.actions.roundEvents

import fwc.game.FWCException

sealed trait UnitDisbandNextStepType

case object UnitDisbandNextStepDeck1 extends UnitDisbandNextStepType {
  override def toString: String = "deck1"
}

case object UnitDisbandNextStepDeck2 extends UnitDisbandNextStepType {
  override def toString: String = "deck2"
}

case object UnitDisbandNextStepPlanningPhase extends UnitDisbandNextStepType {
  override def toString: String = "planningPhase"
}

case object UnitDisbandNextStepCombatCleanUp extends UnitDisbandNextStepType {
  override def toString: String = "combatCleanUp"
}

object UnitDisbandNextStepType {
  def fromString(str: String): UnitDisbandNextStepType =
    str match
      case "deck1" => UnitDisbandNextStepDeck1
      case "deck2" => UnitDisbandNextStepDeck2
      case "planningPhase" => UnitDisbandNextStepPlanningPhase
      case "combatCleanUp" => UnitDisbandNextStepCombatCleanUp
      case s => throw new FWCException(s"Unknown UnitDisbandNextStepType $s")
}
