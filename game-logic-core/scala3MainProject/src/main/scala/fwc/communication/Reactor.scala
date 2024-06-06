package fwc.communication

import fwc.communication.messages.*
import fwc.game.{FWCException, GameState, gameRules}
import fwc.communication.reactions.*
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.gameSaving.GameReplay

import scala.util.{Failure, Success, Try}

object Reactor {

  private var games: Map[String, GameReplay] = Map[String, GameReplay]()

  def apply(message: String): String = {
    val msg = Message.parse(message)
    Try[ujson.Obj] {
      msg match
        case MessageGameAction(userId, gameId, gameAction, messageId) =>
          val gameReplay = games(gameId)
          val (replay: GameReplay, reply: ujson.Value) = ReactionGameAction(userId, gameReplay, gameAction)
          games = games updated (gameId, replay)
          ujson.Obj(
            "action" -> "game_action",
            "gameId" -> ujson.Str(gameId),
            "reply" -> reply
          )
        case MessageTestConnectivity(_, messageId) =>
          ujson.Obj(
            "action" -> "hello",
            "messageId" -> (if messageId != null then messageId else ujson.Null),
          )
        case MessageSaveGame(userId, gameId, saveName, messageId) =>
          ujson.Obj(
            "action" -> "save",
            "userId" -> userId,
            "saveName" -> ReactionSaveGame(userId, gameId, saveName, games(gameId))
          )
        case MessageListSaves(userId, gameId, messageId) =>
          ujson.Obj(
            "action" -> "list_saves",
            "gameId" -> gameId,
            "userId" -> userId,
            "saves" -> ReactionListSavedGames(userId)
          )
        case MessageLoadGame(userId, gameId, saveName, messageId) =>
          //todo: what if game already exists?
          val replay = ReactionLoadGame(userId, saveName)
          games = games updated (gameId, replay)
          ujson.Obj(
            "action" -> "load",
            "gameId" -> gameId,
          )
        case MessageNewGame(userId, gameId, messageId) =>
          ujson.Obj(
            "action" -> "new_game",
            "gameId" -> ujson.Str(gameId),
            "gamesCount" -> ujson.Num(games.size),
          )
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
          )
        case MessageJoinGame(userId, gameId, joinAs, name, messageId) =>
          val settings = games(gameId).gameSettings
          val result = ReactionJoinGame(userId, joinAs, name, settings)
          games = games updated (gameId, games(gameId).copy(gameSettings = result))
          ujson.Obj(
            "action" -> "join_game",
            "gameId" -> ujson.Str(gameId),
            "gameSettings" -> result.toJson,
          )
        case MessageGetGameState(userId, gameId, messageId) =>
          val player =
            if games(gameId).gameSettings.players.nonEmpty then
              games(gameId).gameSettings.players.head.find(_.userId == userId)
            else None
          ujson.Obj(
            "action" -> "get_game_state",
            "gameId" -> ujson.Str(gameId),
            "userId" -> userId,
            "gameRules" -> gameRules.toJson,
            "gameState" -> (
              if player.nonEmpty && player.head.house.nonEmpty
              then games(gameId).currentGameState.toPersonalJson(player.head.house.head)
              else games(gameId).currentGameState.toCleanJson
              ),
          )
        case MessageCreateGame(userId, gameId, isRandomHouses, messageId) =>
          val result = ReactionCreateGame(userId, gameId, isRandomHouses)
          games = games updated (result._1, GameReplay(result._2, result._3.boardCards, result._3, Seq()))
          ujson.Obj(
            "action" -> "create_game",
            //          "userId" -> userId,
            "gameId" -> ujson.Str(result._1),
          )
        case MessageStartGame(userId, gameId, messageId) =>
          val result = ReactionStartGame(userId, games(gameId).gameSettings)
          val state = games(gameId).currentGameState
          games = games updated (gameId, games(gameId).copy(
            gameSettings = result,
            currentGameState = state.copy(subPhase = SubPhaseAddOrder(HouseType.getSeqOfAll))
          ))
          ujson.Obj(
            "action" -> "start_game",
            "gameId" -> ujson.Str(gameId),
            "gameState" -> games(gameId).currentGameState.toCleanJson,
            "gameSettings" -> games(gameId).gameSettings.toJson,
            "gameRules" -> gameRules.toJson,
          )
        case MessageRestoreGames(userId, gameId, messageId, games) =>
          throw new RestoreGamesException(games, messageId)
    } match
      case Success(response: ujson.Obj) => 
        response
        .value.addAll(Map(
            "messageId" -> (if msg.messageId != null then msg.messageId else ujson.Null),
            "type" -> "action"
          ))
        .render(fwc.jsonIndentation)
      case Failure(e: FWCException) =>
        ujson.Obj(
          "action" -> "error",
          "type" -> "action",
          "gameId" -> msg.gameId,
          "userId" -> msg.userId,
          "messageId" -> (if msg.messageId != null then msg.messageId else ujson.Null),
          "message" -> e.getMessage,
          "originalMessageString" -> message,
        ).render(fwc.jsonIndentation)
      case Failure(e) => throw e
  }
  
  def restoreGame(jsonStr: String): Unit = {
    val json = ujson.read(jsonStr)
    val replay = GameReplay.fromJson(json)
    games = games updated (replay.gameSettings.gameId, replay)
  }
  
  def prepareShutdown: Map[String, GameReplay] =
    games
}
