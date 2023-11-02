package fwc.communication.messages

import fwc.game.FWCException
import fwc.game.houses.HouseType

import scala.util.Try

trait Message

object Message {
  def parse(str: String): Message = {
    val json = ujson.read(str)

    val userId = Try[Int](json.obj("userId").num.toInt) getOrElse (throw new FWCException("Message has no userId"))

    val gameId = Try[String](json.obj("gameId").str) getOrElse null

    val action = Try[String](json.obj("action").str) getOrElse (throw new FWCException("Message has no action"))

    if (action != "create_game" && action != "new_game" && action != "hello" && gameId == null)
      throw new FWCException("Message has no gameId")

    action match
      case "hello" => MessageTestConnectivity(userId)
      case "new_game" => MessageNewGame(gameId)
      case "create_game" => MessageCreateGame(userId, gameId)
      case "join_game" =>
        val houseType = Try[HouseType](HouseType.fromString(json.obj("joinAs").str)) getOrElse null
        MessageJoinGame(
          userId,
          gameId,
          houseType
        )
      case "try_join_game" => MessageTryJoinGame(userId, gameId)
      case "game_action" =>
        val gameAction = json("player_action")
        MessageGameAction(userId, gameId, gameAction)
  }
}
