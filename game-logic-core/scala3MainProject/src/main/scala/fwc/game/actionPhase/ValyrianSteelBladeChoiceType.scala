package fwc.game.actionPhase

import fwc.game.FWCException

sealed trait ValyrianSteelBladeChoiceType



object ValyrianSteelBladeChoiceType {
  case object Nothing extends ValyrianSteelBladeChoiceType {
    override def toString: String = "nothing"
  }

  case object PlusOne extends ValyrianSteelBladeChoiceType {
    override def toString: String = "plusOne"
  }

  case object ChangeTOBCard extends ValyrianSteelBladeChoiceType {
    override def toString: String = "changeTOBCard"
  }

  def fromString(str: String): ValyrianSteelBladeChoiceType =
    str match
      case "nothing" => Nothing
      case "plusOne" => PlusOne
      case "changeTOBCard" => ChangeTOBCard
      case s => throw new FWCException(s"Unknown Valyrian Steel Blade owner choice '$s'")
}
