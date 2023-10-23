package fwc.communication

import fwc.GameSettings
import fwc.communication.messages.*
import fwc.game.GameState
import fwc.communication.reactions.*
import fwc.gameSaving.GameReplay
//import org.zeromq.ZLoop

object Reactor {

  private var games: Map[String, GameReplay] = Map[String, GameReplay]()

  def apply(message: String): String = {
    val msg = Message.parse(message)
    msg match
      case m: MessageTestConnectivity =>
        ujson.Obj(
          "action" -> "hello"
        ).render(fwc.jsonIndentation)
      case MessageNewGame(gameId) =>
        ujson.Obj(
            "action" -> "new_game",
          "gameId" -> ujson.Str(gameId),
          "gamesCount" -> ujson.Num(games.size),
        ).render(fwc.jsonIndentation)
      case MessageCreateGame(userId, gameId) =>
        val result = ReactionCreateGame(userId, gameId)
        games = games + (result._1 -> GameReplay(result._2, result._3.boardCards, result._3, Seq()))
        ujson.Obj(
          "gameId" -> ujson.Str(result._1),
          "gameState" -> result._3.toJson
        ).render(fwc.jsonIndentation)
      case MessageGameAction(userId, gameId, gameAction) =>
        val gameReplay = games(gameId)
        val (replay: GameReplay, reply: ujson.Value) = ReactionGameAction.apply(userId, gameReplay, gameAction)
        games = games + (gameId -> replay)
        reply.render(fwc.jsonIndentation)
  }
}
