package fwc.game.planningPhase

sealed trait OrderType

object OrderType {
  case object March extends OrderType {
    override def toString = "march"
  }

  case object Defend extends OrderType {
    override def toString = "defend"
  }

  case object Support extends OrderType {
    override def toString = "support"
  }

  case object Raid extends OrderType {
    override def toString = "raid"
  }

  case object ConsolidatePower extends OrderType {
    override def toString = "consolidatePower"
  }

  def fromString(str: String): OrderType = {
    str match {
      case "march" => March
      case "defend" => Defend
      case "support" => Support
      case "raid" => Raid
      case "consolidatePower" => ConsolidatePower
      case _ => throw new RuntimeException(s"Unknown order type $str")
    }
  }
}
