package fwc
import ujson.Value

import java.util.UUID
import scala.util.{Failure, Success, Try}

case class GameSettings(
                       gameId: String, //note: this is actually the lobby id
                       gameUuid: UUID,
                       ownerId: Int,
                       isInputOnly: Boolean,
                       isRandomHouses: Boolean,
                       isRandomEventsServerSide: Boolean,
                       players: Option[Seq[Player]],
                       playersInputting: Option[Seq[PlayerInputting]],
                       ) extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "gameId" -> gameId,
    "gameUuid" -> gameUuid.toString,
    "ownerId" -> ownerId,
    "isInputOnly" -> isInputOnly,
    "isRandomHouses" -> isRandomHouses,
    "isRandomEventsServerSide" -> isRandomEventsServerSide,
    "players" -> (if players.nonEmpty then ujson.Arr.from(
      players.head.map(_.toJson)
    ) else ujson.Null),
    "playersInputting" ->(if playersInputting.nonEmpty then ujson.Arr.from(
      playersInputting.head.map(_.toJson)
    ) else ujson.Null)
  )
}

object GameSettings extends JsonParsable {
  override def fromJson(json: Value): GameSettings =
    GameSettings(
      json("gameId").str,
      Try[UUID](UUID.fromString(json("gameUuid").str)) match
        case Success(value) => value
        case Failure(exception) => UUID.randomUUID()
      , 
      json("ownerId").num.toInt,
      json("isInputOnly").bool,
      json("isRandomHouses").bool,
      json("isRandomEventsServerSide").bool,
      json("players").arrOpt.map(_.map(p => Player.fromJson(p)).toSeq),
      json("playersInputting").arrOpt.map(_.map(p => PlayerInputting.fromJson(p)).toSeq),
    )
}
