package fwc.game.planningPhase

sealed trait OrderType

case object OrderMarch extends OrderType {
  override def toString = "march"
}

case object OrderDefend extends OrderType {
  override def toString = "defend"
}

case object OrderSupport extends OrderType {
  override def toString = "support"
}

case object OrderRaid extends OrderType {
  override def toString = "raid"
}

case object OrderConsolidatePower extends OrderType {
  override def toString = "consolidatePower"
}

object OrderType {
  def fromString(str: String): OrderType = {
    str match {
      case "march" => OrderMarch
      case "defend" => OrderDefend
      case "support" => OrderSupport
      case "raid" => OrderRaid
      case "consolidatePower" => OrderConsolidatePower
      case _ => throw new RuntimeException(s"Unknown order type $str")
    }
  }
}
