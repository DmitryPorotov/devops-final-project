package fwc.game.board

import fwc.game.FWCException

sealed trait DominanceTokenType



object DominanceTokenType {
  case object ValyrianSword extends DominanceTokenType {
    override def toString: String = "valyrianSword"
  }

  case object MessengerRaven extends DominanceTokenType {
    override def toString: String = "messengerRaven"
  }
  
  def fromString(str: String): DominanceTokenType = str match
    case "valyrianSword" => ValyrianSword
    case "messengerRaven" => MessengerRaven
    case s => throw new FWCException(s"Unknown DominanceTokenType string $s")
}