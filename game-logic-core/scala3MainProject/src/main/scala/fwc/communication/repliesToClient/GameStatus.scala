package fwc.communication.repliesToClient

import fwc.JsonSerializable
import ujson.{Obj, Value}



case class GameStatus(
                      created: Boolean,
                      details: Value
                     ) extends JsonSerializable {
  override def toJson: Obj = {
    Obj(
      "created" -> created,
      "details" -> details
    )
  }
}
