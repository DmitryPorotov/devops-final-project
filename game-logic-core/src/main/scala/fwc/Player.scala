package fwc

import fwc.game.houses.HouseType
import ujson.Value

case class Player(userId: Int, house: HouseType) extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "userId" -> userId,
    "house" -> house.toString
  )
}

object Player extends JsonParsable {
  override def fromJson(json: Value): Player =
    Player(
      json("userId").num.toInt,
      HouseType.fromString(json("house").str)
    )
}
