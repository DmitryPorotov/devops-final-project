package fwc.communication

import fwc.GameSettings
import fwc.communication.messages.*
import fwc.game.{FWCException, GameState, gameRules}
import fwc.communication.reactions.*
import fwc.gameSaving.GameReplay
//import org.zeromq.ZLoop

object Reactor {

  private var games: Map[String, GameReplay] = Map[String, GameReplay]()

  def apply(message: String): String = {
    val msg = Message.parse(message)
    msg match
      case MessageGameAction(userId, gameId, gameAction, messageId) =>
        val gameReplay = games(gameId)
        val (replay: GameReplay, reply: ujson.Value) = ReactionGameAction.apply(userId, gameReplay, gameAction)
        games = games + (gameId -> replay)
        ujson.Obj(
          "gameId" -> ujson.Str(gameId),
          "messageId" -> messageId,
          "reply" -> reply
        ).render(fwc.jsonIndentation)
      case MessageGetRules(userId, gameId, messageId) =>
        ujson.Obj(
          "userId" -> userId,
          "gameId" -> ujson.Str(gameId),
          "messageId" -> messageId,
          "gameRules" -> gameRules.toJson
        )render fwc.jsonIndentation
      case m: MessageTestConnectivity =>
        ujson.Obj(
          "action" -> "hello"
        ).render(fwc.jsonIndentation)
      case MessageNewGame(gameId, messageId) =>
        ujson.Obj(
          "action" -> "new_game",
          "gameId" -> ujson.Str(gameId),
          "messageId"->messageId,
          "gamesCount" -> ujson.Num(games.size),
        ).render(fwc.jsonIndentation)
      case MessageJoinGame(userId, gameId, joinAs, messageId) =>
        val settings = games(gameId).gameSettings
        if !settings.isRandomHouses then
          if joinAs.isEmpty then throw new FWCException("Must choose a house")
          if joinAs.isDefined
            && settings.players.isDefined
            && settings.players.head.nonEmpty
            && settings.players.head.foldLeft(false)(
              (acc, player) =>
                if player.house == joinAs.head then
                  true
                else false
            )
          then
            throw new FWCException("Other player has selected this house already")
        val result = ReactionJoinGame(userId, joinAs, settings)
        games = games + (gameId -> games(gameId).copy(gameSettings = result))
        ujson.Obj(
          "gameId" -> ujson.Str(gameId),
          "messageId"->messageId,
          "gameSettings" -> result.toJson
        ).render(fwc.jsonIndentation)
      case MessageCreateGame(userId, gameId, isRandomHouses, messageId) =>
        val result = ReactionCreateGame(userId, gameId, isRandomHouses)
        games = games + (result._1 -> GameReplay(result._2, result._3.boardCards, result._3, Seq()))
        ujson.Obj(
          "gameId" -> ujson.Str(result._1),
          "messageId"->messageId,
          "gameState" -> result._3.toCleanJson
        ).render(fwc.jsonIndentation)
      case MessageStartGame(userId, gameId, messageId) =>
        val result = ReactionStartGame(userId, games(gameId).gameSettings)
        games = games + (gameId -> games(gameId).copy(gameSettings = result))
        ujson.Obj(
          "gameId" -> ujson.Str(gameId),
          "messageId"->messageId,
          "gameState" -> games(gameId).currentGameState.toCleanJson,
          "gameSettings" -> games(gameId).gameSettings.toJson
        ).render(fwc.jsonIndentation)

  }
}
