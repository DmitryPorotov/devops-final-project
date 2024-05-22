package fwc.game.board

import fwc.game.FWCException

sealed trait DominanceTokenType

case object DominanceTokenValyrianSword extends DominanceTokenType {
  override def toString: String = "valyrianSword"
}

case object DominanceTokenMessengerRaven extends DominanceTokenType {
  override def toString: String = "messengerRaven"
}

object DominanceTokenType {
  def fromString(str: String): DominanceTokenType = str match
    case "valyrianSword" => DominanceTokenValyrianSword
    case "messengerRaven" => DominanceTokenMessengerRaven
    case s => throw new FWCException(s"Unknown DominanceTokenType string $s")
}