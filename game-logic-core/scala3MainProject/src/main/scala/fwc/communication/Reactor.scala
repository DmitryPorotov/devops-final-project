package fwc.communication

import fwc.communication.messagesFromClient.*
import fwc.game.{FWCException, GameState, gameRules}
import fwc.communication.reactions.*
import fwc.communication.repliesToClient.*
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.gameSaving.GameReplay

import scala.util.{Failure, Success, Try}

object Reactor {

  private var games: Map[String, GameReplay] = Map[String, GameReplay]()
  private def getGame(id: String): GameReplay = {
    if games.contains(id) then 
      games(id)
    else throw new RuntimeException(s"Game id '$id' does not exist.")
  }

  def apply(msg: Message, json: ujson.Value): String = {
    Try[Reply] {
      msg match
        case MessageGameAction(userId, gameId, gameAction, messageId) =>
          val gameReplay = getGame(gameId)
          val (replay: GameReplay, reply: ujson.Value) = ReactionGameAction(userId, gameReplay, gameAction)
          games = games updated (gameId, replay)
          ReplyGameAction(gameId, reply, messageId)

        case MessageTestConnectivity(_, messageId) =>
          ReplyTestConnectivity(messageId)

        case MessageSaveGame(userId, gameId, saveName, messageId) =>
          ReplySaveGame(userId, gameId,ReactionSaveGame(userId, gameId, saveName, getGame(gameId)), messageId)

        case MessageListSaves(userId, gameId, messageId) =>
          ReplyListSaves(userId, gameId, ReactionListSavedGames(userId), messageId)

        case MessageLoadGame(userId, gameId, saveName, messageId) =>
          //todo: how do I handle setting change? Not the same players and in the saved file? Different houses?
          // need to support changing settings after load
          val replay = ReactionLoadGame(userId, saveName)
          //val settings = games(gameId).gameSettings
          games = games updated (gameId, replay)
          ReplyLoadGame(gameId, messageId)

        case MessageNewGame(userId, gameId, messageId) =>
          ReplyNewGame(gameId, games.size, messageId)

        case MessageGetStatus(userId, gameId, messageId) =>
          val gameReplay = Try {
            games(gameId)
          } match
            case Success(gr) => gr
            case Failure(e) => null
          ReplyGetStatus(userId, gameId, gameReplay, messageId)

        case MessageJoinGame(userId, gameId, joinAs, name, messageId) =>
          val settings = getGame(gameId).gameSettings
          val result = ReactionJoinGame(userId, joinAs, name, settings)
          games = games updated (gameId, getGame(gameId).copy(gameSettings = result))
          ReplyJoinGame(userId, gameId, result, messageId)

        case MessageGetGameState(userId, gameId, messageId) =>
          val game = getGame(gameId)
          ReplyGetGameState(userId, gameId, gameRules, game.currentGameState, game.gameSettings, messageId)

        case MessageCreateGame(userId, gameId, isRandomHouses, isInputOnly, messageId) =>
          val (id, settings, state) = ReactionCreateGame(userId, gameId, isRandomHouses, isInputOnly)
          games = games updated (id, GameReplay(settings, state.boardCards, state, Seq()))
          ReplyCreateGame(gameId, messageId)

        case MessageStartGame(userId, gameId, messageId) =>
          val result = ReactionStartGame(userId, getGame(gameId).gameSettings)
          val state = getGame(gameId).currentGameState
          games = games updated (gameId, getGame(gameId).copy(
            gameSettings = result,
            currentGameState = state.copy(subPhase = SubPhaseAddOrder(HouseType.getSeqOfAll))
          ))
          ReplyStartGame(gameId, messageId)

        case MessageRestoreGames(userId, gameId, messageId, games) =>
          throw new RestoreGamesException(games, messageId)
    } match
      case Success(reply: Reply) =>
        reply.toJsonString
      case Failure(e: FWCException) =>
        ReplyError(msg.userId, msg.gameId, e.getMessage, json, msg.messageId).toJsonString
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
