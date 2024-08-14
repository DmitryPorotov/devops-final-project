package fwc.game.board

import fwc.JsonSerializable
import fwc.game.FWCException
import ujson.Value

sealed trait MilitaryUnitType extends JsonSerializable {
  def musteringPoints: Int = 1
  def strength = 1
  def canRetreat = true
  def canBeMustered: Boolean = musteringPoints > 0
  override def toJson: Value = ujson.Obj(
    "musteringPoints" -> musteringPoints,
    "strength" -> strength,
    "canRetreat" -> canRetreat,
  )
}



object MilitaryUnitType {
  case object Footmen extends MilitaryUnitType {
    override def toString: String = "footmen"
  }

  case object Knights extends MilitaryUnitType {
    override def toString: String = "knights"

    override def musteringPoints = 2

    override def strength: Int = 2
  }

  case object Ships extends MilitaryUnitType {
    override def toString: String = "ships"
  }

  case object SiegeEngines extends MilitaryUnitType {
    override def toString: String = "siegeEngines"

    override def musteringPoints = 2

    override def strength: Int = 4

    override def canRetreat: Boolean = false
  }

  case object Garrison extends MilitaryUnitType {
    override def toString: String = "garrison"

    override def musteringPoints: Int = -1

    override def strength: Int = 0

    override def canRetreat: Boolean = false
  }

  case object PowerToken extends MilitaryUnitType {
    override def toString: String = "powerToken"

    override def musteringPoints: Int = -1

    override def strength: Int = 0

    override def canRetreat: Boolean = false
  }

  def fromString(str: String): MilitaryUnitType = {
    str match
      case "footmen" => Footmen
      case "knights" => Knights
      case "ships" => Ships
      case "siegeEngines" => SiegeEngines
      case "garrison" => Garrison
      case "powerToken" => PowerToken
      case s => throw new FWCException(s"Unknown MilitaryUnitType $s")
  }
}