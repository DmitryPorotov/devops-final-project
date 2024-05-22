package fwc.game.eventsPhase.cards

import fwc.game.FWCException

sealed trait EventCardChoiceType

case object EventCardChoiceA extends EventCardChoiceType {
  override def toString: String = "a"
}

case object EventCardChoiceB extends EventCardChoiceType {
  override def toString: String = "b"
}

case object EventCardChoiceC extends EventCardChoiceType {
  override def toString: String = "c"
}

object EventCardChoiceType {
  def fromString(str: String): EventCardChoiceType = {
    str match
      case "a" => EventCardChoiceA
      case "b" => EventCardChoiceB
      case "c" => EventCardChoiceC
      case s => throw new FWCException(s"Unknown event card choice $s")
  }
}
