package fwc.communication.messagesFromClient

import fwc.JsonSerializable
import fwc.game.FWCException
import fwc.game.houses.HouseType

import scala.util.{Failure, Success, Try}

class Message(
               val userId: Int,
               val gameId: String,
               val messageId: String
             ) extends JsonSerializable {
  def toJson: ujson.Obj = {
    ujson.Obj(
      "userId" -> userId,
      "gameId" -> gameId,
      "messageId" -> messageId,
      "type" -> "action",
      "action" -> "dummy_action"
    )
  }
}

object Message {
  def parse(str: String): (Message, ujson.Value) = {
    val json = ujson.read(str)

    val gameId = Try[String](json.obj("gameId").str) getOrElse null

    val messageId = Try[String](json.obj("messageId").str) getOrElse null

    val userId = Try[Int](json.obj("userId").num.toInt) getOrElse (throw new FWCException("Message has no userId", gameId, messageId))

    val action = Try[String](json.obj("action").str) getOrElse (throw new FWCException("Message has no action", gameId, userId, messageId))

    if (action != "create_game" && action != "new_game" && action != "hello" && gameId == null)
      throw new FWCException("Message has no gameId", null, userId)

    val message = action match
      case "game_action" =>
        val gameAction = json("player_action")
        MessageGameAction(userId, gameId, gameAction, messageId)
      case "hello" => MessageTestConnectivity(userId, messageId)
      case "save" =>
        val saveName = json("saveName").str
        MessageSaveGame(userId, gameId, saveName, messageId)
      case "get_status" => MessageGetStatus(userId, gameId, messageId)
      case "load" =>
        val saveName = json("saveName").str
        MessageLoadGame(userId, gameId, saveName, messageId)
      case "list_saves" => MessageListSaves(userId, gameId, messageId)
      case "new_game" => MessageNewGame(userId, gameId, messageId)
      case "create_game" =>
        val isRandomHouses = Try[Boolean](json.obj("isRandomHouses").bool) getOrElse true
        val isInputOnly = Try[Boolean](json.obj("isInputOnly").bool) getOrElse false
        MessageCreateGame(userId, gameId, isRandomHouses, isInputOnly ,messageId)
      case "join_game" =>
        val name = Try(json.obj("name").str) match
          case Success(value) => value
          case Failure(_) => throw new FWCException("Message has no player's name", gameId, userId, messageId)
        val houseType = Try[HouseType](HouseType.fromString(json.obj("joinAs").str)) match
          case Success(s) => Some(s)
          case Failure(_) => None
        MessageJoinGame(
          userId,
          gameId,
          houseType,
          name,
          messageId,
        )
      case "get_game_state" => MessageGetGameState(userId, gameId, messageId)
      case "restore_games" => 
        val games = Try[List[String]](json.obj("games").arr.map(_.str).toList) getOrElse null
        MessageRestoreGames(userId, gameId, messageId, games)
      case "start_game" => MessageStartGame(userId, gameId, messageId)
      case "try_join_game" => MessageTryJoinGame(userId, gameId, messageId)
      case a => throw new FWCException(s"Unknown action $a", gameId, userId, messageId)
    (message, json)
  }
}
