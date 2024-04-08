package fwc.communication

import fwc.GameSettings
import fwc.communication.messages.*
import fwc.game.{FWCException, GameState, gameRules}
import fwc.communication.reactions.*
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.gameSaving.GameReplay
import fwc.gameSaving.actions.ActionException

import scala.util.{Failure, Success, Try}
//import org.zeromq.ZLoop

object Reactor {

  private var games: Map[String, GameReplay] = Map[String, GameReplay]()

  def apply(message: String): String = {
    val msg = Message.parse(message)
    Try[String] {
      msg match
        case MessageGameAction(userId, gameId, gameAction, messageId) =>
          val gameReplay = games(gameId)
          val (replay: GameReplay, reply: ujson.Value) = ReactionGameAction(userId, gameReplay, gameAction)
          games = games + (gameId -> replay)
          ujson.Obj(
            "action" -> "game_action",
            "gameId" -> ujson.Str(gameId),
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "reply" -> reply
          ).render(fwc.jsonIndentation)
        case MessageTestConnectivity(_, messageId) =>
          ujson.Obj(
            "action" -> "hello",
            "messageId" -> (if messageId != null then messageId else ujson.Null),
          ).render(fwc.jsonIndentation)
        case MessageSaveGame(userId, gameId, saveName, messageId) =>
          ujson.Obj(
            "action" -> "save",
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "userId" -> userId,
            "saveName" -> ReactionSaveGame(userId, gameId, saveName, games(gameId))
          ).render(fwc.jsonIndentation)
        case MessageListSaves(userId, gameId, messageId) =>
          ujson.Obj(
            "action" -> "list_saves",
            "gameId" -> gameId,
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "userId" -> userId,
            "saves" -> ReactionListSavedGames(userId)
          ).render(fwc.jsonIndentation)
        case MessageLoadGame(userId, gameId, saveName, messageId) =>
          //todo: what if game already exists?
          val replay = ReactionLoadGame(userId, saveName)
          games = games + (gameId -> replay)
          ujson.Obj(
            "action" -> "load",
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "gameId" -> gameId,
          ).render(fwc.jsonIndentation)
        case MessageNewGame(userId, gameId, messageId) =>
          ujson.Obj(
            "action" -> "new_game",
            "gameId" -> ujson.Str(gameId),
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "gamesCount" -> ujson.Num(games.size),
          ).render(fwc.jsonIndentation)
        case MessageGetStatus(userId, gameId, messageId) =>
          val gameReplay = Try {
            games(gameId)
          } match
            case Success(gr) => gr
            case Failure(e) => null
          val status = gameReplay != null
          val details =
            if status
            then ujson.Obj(
              "roundCounter" -> gameReplay.currentGameState.roundCounter,
              "gameSettings" -> gameReplay.gameSettings.toJson,
              "subPhase" -> gameReplay.currentGameState.subPhase.toJson,
            )
            else ujson.Obj()
          ujson.Obj(
            "action" -> "get_status",
            "gameId" -> gameId,
            "userId" -> userId,
            "status" -> ujson.Obj(
              "created" -> status,
              "details" -> details,
            ),
            "messageId" -> (if messageId != null then messageId else ujson.Null),
          ).render(fwc.jsonIndentation)
        case MessageJoinGame(userId, gameId, joinAs, name, messageId) =>
          val settings = games(gameId).gameSettings
          val result = ReactionJoinGame(userId, joinAs, name, settings)
          games = games + (gameId -> games(gameId).copy(gameSettings = result))
          ujson.Obj(
            "action" -> "join_game",
            "gameId" -> ujson.Str(gameId),
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "gameSettings" -> result.toJson,
          ).render(fwc.jsonIndentation)
        case MessageGetGameState(userId, gameId, messageId) =>
          val player =
            if games(gameId).gameSettings.players.nonEmpty then
              games(gameId).gameSettings.players.head.find(_.userId == userId)
            else None
          ujson.Obj(
            "action" -> "get_game_state",
            "gameId" -> ujson.Str(gameId),
            "userId" -> userId,
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "gameRules" -> gameRules.toJson,
            "gameState" -> (
              if player.nonEmpty && player.head.house.nonEmpty
              then games(gameId).currentGameState.toPersonalJson(player.head.house.head)
              else games(gameId).currentGameState.toCleanJson
              ),
          ).render(fwc.jsonIndentation)
        case MessageCreateGame(userId, gameId, isRandomHouses, messageId) =>
          val result = ReactionCreateGame(userId, gameId, isRandomHouses)
          games = games + (result._1 -> GameReplay(result._2, result._3.boardCards, result._3, Seq()))
          ujson.Obj(
            "action" -> "create_game",
            //          "userId" -> userId,
            "gameId" -> ujson.Str(result._1),
            "messageId" -> (if messageId != null then messageId else ujson.Null),
          ).render(fwc.jsonIndentation)
        case MessageStartGame(userId, gameId, messageId) =>
          val result = ReactionStartGame(userId, games(gameId).gameSettings)
          val state = games(gameId).currentGameState
          games = games + (gameId -> games(gameId).copy(
            gameSettings = result,
            currentGameState = state.copy(subPhase = SubPhaseAddOrder(HouseType.getSeqOfAll))
          ))
          ujson.Obj(
            "action" -> "start_game",
            "gameId" -> ujson.Str(gameId),
            "messageId" -> (if messageId != null then messageId else ujson.Null),
            "gameState" -> games(gameId).currentGameState.toCleanJson,
            "gameSettings" -> games(gameId).gameSettings.toJson,
            "gameRules" -> gameRules.toJson,
          ).render(fwc.jsonIndentation)
    } match
      case Success(response: String) => response
      case Failure(e: FWCException) =>
        ujson.Obj(
          "action" -> "error",
          "gameId" -> msg.gameId,
          "userId" -> msg.userId,
          "messageId" -> (if msg.messageId != null then msg.messageId else ujson.Null),
          "message" -> e.getMessage,
          "originalMessageString" -> message,
        ).render(fwc.jsonIndentation)
      case Failure(e) => throw e
  }
}
