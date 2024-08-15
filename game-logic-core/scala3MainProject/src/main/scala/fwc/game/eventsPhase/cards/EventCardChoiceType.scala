package fwc.game.eventsPhase.cards

import fwc.game.FWCException

sealed trait EventCardChoiceType



object EventCardChoiceType {
  case object ChoiceA extends EventCardChoiceType {
    override def toString: String = "a"
  }

  case object ChoiceB extends EventCardChoiceType {
    override def toString: String = "b"
  }

  case object ChoiceC extends EventCardChoiceType {
    override def toString: String = "c"
  }

  def fromString(str: String): EventCardChoiceType = {
    str match
      case "a" => ChoiceA
      case "b" => ChoiceB
      case "c" => ChoiceC
      case s => throw new FWCException(s"Unknown event card choice $s")
  }
}
