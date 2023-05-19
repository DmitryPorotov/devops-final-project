package fwc.gameLoading

import fwc.JsonSerializable
import ujson.Value

case class WildlingCard(
                         code: Int,
                         title: String,
                         wildlingVictoryLowestBidderText: String,
                         wildlingVictoryEveryoneElseText: String,
                         playerVictoryText: String
                       ) extends CardTrait with JsonSerializable{
  override def getCode: Int = code

  override def toJson: Value = ujson.Obj(
    "code" -> code,
    "title" -> title,
    "wildlingVictoryLowestBidderText" -> wildlingVictoryEveryoneElseText,
    "wildlingVictoryEveryoneElseText" -> wildlingVictoryEveryoneElseText,
    "playerVictoryText" -> playerVictoryText,
  )
}

