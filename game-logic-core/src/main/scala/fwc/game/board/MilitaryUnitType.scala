package fwc.game.board

import fwc.game.FWCException

sealed trait MilitaryUnitType {
  def musteringPoints: Int = 1
  def strength = 1
  def canRetreat = true
  def canBeMustered: Boolean = musteringPoints > 0
}

case object MilitaryUnitFootmen extends MilitaryUnitType {
  override def toString: String = "footmen"
}

case object MilitaryUnitKnights extends MilitaryUnitType {
  override def toString: String = "knights"
  override def musteringPoints = 2

  override def strength: Int = 2
}

case object MilitaryUnitShips extends MilitaryUnitType {
  override def toString: String = "ships"
}

case object MilitaryUnitSiegeEngines extends MilitaryUnitType {
  override def toString: String = "siegeEngines"
  override def musteringPoints = 2

  override def strength: Int = 4

  override def canRetreat: Boolean = false
}

case object MilitaryUnitGarrison extends MilitaryUnitType {
  override def toString: String = "garrison"
  override def musteringPoints = -1
  override def strength: Int = 0

  override def canRetreat: Boolean = false
}

case object MilitaryUnitPowerToken extends MilitaryUnitType {
  override def toString: String = "powerToken"
  override def musteringPoints = -1

  override def strength: Int = 0

  override def canRetreat: Boolean = false
}

object MilitaryUnitType {
  def fromString(str: String): MilitaryUnitType = {
    str match
      case "footmen" => MilitaryUnitFootmen
      case "knights" => MilitaryUnitKnights
      case "ships" => MilitaryUnitShips
      case "siegeEngines" => MilitaryUnitSiegeEngines
      case "garrison" => MilitaryUnitGarrison
      case "powerToken" => MilitaryUnitPowerToken
      case s => throw new FWCException(s"Unknown MilitaryUnitType $s")
  }
}