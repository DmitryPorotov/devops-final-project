package fwc.game.planningPhase

import fwc.JsonSerializable
import scala.util.Try

case class Order(orderType: OrderType, isStar: Boolean = false, modifier: Int = 0) extends JsonSerializable {
  
  def toJson: ujson.Value = {
    val order = ujson.Obj(
      "type" -> ujson.Str(orderType.toString)
    )
    if isStar then order.obj addOne ("isStar" -> ujson.Bool(isStar))
    if modifier != 0 then order.obj addOne ("modifier" -> ujson.Num(modifier))
    order
  }

}

object Order {
  def fromJson(json: ujson.Value): Order = {
    Order(
      OrderType.fromString(json("type").str),
      Try[Boolean](json.obj("isStar").bool).getOrElse(false),
      Try[Double](json.obj("modifier").num).getOrElse(.0).toInt
    )
  }
}
