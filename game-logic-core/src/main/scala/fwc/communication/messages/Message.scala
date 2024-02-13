package fwc.communication.messages

import fwc.game.FWCException
import fwc.game.houses.HouseType

import scala.util.{Try, Success, Failure}

trait Message

object Message {
  def parse(str: String): Message = {
    val json = ujson.read(str)

    val userId = Try[Int](json.obj("userId").num.toInt) getOrElse (throw new FWCException("Message has no userId"))

    val gameId = Try[String](json.obj("gameId").str) getOrElse null

    val action = Try[String](json.obj("action").str) getOrElse (throw new FWCException("Message has no action"))

    val messageId = Try[String](json.obj("messageId").str) getOrElse null

    if (action != "create_game" && action != "new_game" && action != "hello" && gameId == null)
      throw new FWCException("Message has no gameId")

    action match
      case "game_action" =>
        val gameAction = json("player_action")
        MessageGameAction(userId, gameId, gameAction, messageId)
      case "hello" => MessageTestConnectivity(userId, messageId)
      case "save" =>
        val saveName = json("saveName").str
        MessageSaveGame(userId, gameId, saveName, messageId)
      case "list_saves" => MessageListSaves(userId, gameId, messageId)
      case "new_game" => MessageNewGame(gameId, messageId)
      case "create_game" =>
        val isRandomHouses = Try[Boolean](json.obj("isRandomHouses").bool) getOrElse true
        MessageCreateGame(userId, gameId, isRandomHouses, messageId)
      case "join_game" =>
        val name = Try(json.obj("name").str) match
          case Success(value) => value
          case Failure(_) => throw new FWCException("Message has no player's name")
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
      case "start_game" => MessageStartGame(userId, gameId, messageId)
      case "try_join_game" => MessageTryJoinGame(userId, gameId, messageId)
  }
}
