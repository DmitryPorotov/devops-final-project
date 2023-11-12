package fwc

import fwc.game.houses.HouseType
import ujson.Value
import scala.util.{Try, Success, Failure}

case class Player(userId: Int, house: Option[HouseType]) extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "userId" -> userId,
    "house" -> house.orNull.toString
  )
}

object Player extends JsonParsable {
  override def fromJson(json: Value): Player =
    Player(
      json("userId").num.toInt,
      Try[HouseType](HouseType.fromString(json("house").str)) match
        case Success(s) => Some(s)
        case Failure(e) => None
    )
}
