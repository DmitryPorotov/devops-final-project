package fwc.gameLoading

import fwc.JsonSerializable
import ujson.*

case class RoundEventCard(
                           code: Int, 
                           title: String, 
                           text: String,
                           wildlings: Int
                         ) extends CardTrait, JsonSerializable {
  override def getCode: Int = code

  override def toJson: Value = {
    Obj(
      "code" -> code,
      "title" -> title,
      "text" -> text,
      "wildlings" -> wildlings,
    )
  }
}
