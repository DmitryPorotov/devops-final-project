package fwc.game.actionPhase

import fwc.game.FWCException

sealed trait RavenChoiceType

case object RavenChoiceNothing extends RavenChoiceType {
  override def toString: String = "nothing"
}

case object RavenChoiceChangeOrder extends RavenChoiceType {
  override def toString: String = "changeOrder"
}

case object RavenChoiceLookAtWildlingsCard extends RavenChoiceType {
  override def toString: String = "lookAtWildlingsCard"
}

object RavenChoiceType {
  def fromString(str: String): RavenChoiceType = str match
    case "nothing" => RavenChoiceNothing
    case "changeOrder" => RavenChoiceChangeOrder
    case "lookAtWildlingsCard" => RavenChoiceLookAtWildlingsCard
    case s => throw new FWCException(s"Unknown raven choice $s")
}
