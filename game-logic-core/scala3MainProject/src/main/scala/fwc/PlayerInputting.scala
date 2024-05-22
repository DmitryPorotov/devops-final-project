package fwc

import fwc.game.houses.HouseType
import ujson.Value

case class PlayerInputting(
                          userId: Int,
                          forHouses: Seq[HouseType]
                          ) extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "userId" -> userId,
    "forHouses" -> ujson.Arr.from(
      forHouses.map(_.toString)
    )
  )
}

object PlayerInputting extends JsonParsable {
  override def fromJson(json: Value): PlayerInputting =
    PlayerInputting(
      json("userId").num.toInt,
      json("forHouses").arr.map(h => HouseType.fromString(h.str)).toSeq
    )
}
