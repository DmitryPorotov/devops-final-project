package fwc.communication.repliesToClient

import fwc.GameSettings
import fwc.game.{GameRules, GameState}

case class ReplyGetGameState(
                              userId: Int,
                              gameId: String,
                              gameRules: GameRules,
                              gameState: GameState,
                              gameSettings: GameSettings,
                              messageId: String,
                            ) extends Reply {
  def toJson: ujson.Obj = {
    addUserIdActionGameIdMessageId(userId, "get_game_state", gameId, messageId)
    val player =
      if gameSettings.players.nonEmpty then
        gameSettings.players.head.find(_.userId == userId)
      else None
    val inputtingPlayer =
      if gameSettings.isInputOnly && gameSettings.playersInputting.nonEmpty
      then gameSettings.playersInputting.head.find(_.userId == userId)
      else None
    json.value.addAll(Map(
      "gameRules" -> gameRules.toJson,
      "gameState" -> (
        if userId < 0 then
          gameState.toJson
        else if gameSettings.isInputOnly && inputtingPlayer.nonEmpty then
          gameState.toJsonForInputtingPlayer(inputtingPlayer.head.forHouses)
        else if player.nonEmpty && player.head.house.nonEmpty
        then gameState.toPersonalJson(player.head.house.head)
        else gameState.toCleanJson
        ),
    ))
  }
}
