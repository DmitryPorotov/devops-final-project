package fwc.game.actionPhase

import fwc.game.FWCException

sealed trait ValyrianSteelBladeChoiceType

case object ValyrianSteelBladeChoiceNothing extends ValyrianSteelBladeChoiceType {
  override def toString: String = "nothing"
}

case object ValyrianSteelBladeChoicePlusOne extends ValyrianSteelBladeChoiceType {
  override def toString: String = "plusOne"
}

case object ValyrianSteelBladeChoiceChangeTOBCard extends ValyrianSteelBladeChoiceType {
  override def toString: String = "changeTOBCard"
}

object ValyrianSteelBladeChoiceType {
  def fromString(str: String): ValyrianSteelBladeChoiceType =
    str match
      case "nothing" => ValyrianSteelBladeChoiceNothing
      case "plusOne" => ValyrianSteelBladeChoicePlusOne
      case "changeTOBCard" => ValyrianSteelBladeChoiceChangeTOBCard
      case s => throw new FWCException(s"Unknown Valyrian Steel Blade owner choice '$s'")
}
