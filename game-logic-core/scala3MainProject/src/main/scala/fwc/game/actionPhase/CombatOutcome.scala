package fwc.game.actionPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.HouseType
import ujson.Value

case class CombatOutcome(
                          attackerStrength: Int,
                          defenderStrength: Int,
                          winner: Option[HouseType],
                          attackerUnitsToKill: Int,
                          defenderUnitsToKill: Int,
                        ) extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "attackerStrength" -> attackerStrength,
    "defenderStrength" -> defenderStrength,
    "winner" -> (if winner.isEmpty then ujson.Null else winner.head.toString),
    "attackerUnitsToKill" -> attackerUnitsToKill,
    "defenderUnitsToKill" -> defenderUnitsToKill
  )
}

object CombatOutcome extends JsonParsable {
  override def fromJson(json: Value): CombatOutcome = {
    CombatOutcome(
      json("attackerStrength").num.toInt,
      json("defenderStrength").num.toInt,
      if json("winner") == ujson.Null then None else Some(HouseType.fromString(json("winner").str)),
      json("attackerUnitsToKill").num.toInt,
      json("defenderUnitsToKill").num.toInt,
    )
  }
}
