package fwc.communication.repliesToClient

import fwc.GameSettings
import fwc.game.{GameRules, GameState, GameStateParts}
import ujson.Value

case class ReplyGetPartialGameState(userId: Int,
                                    gameId: String,
                                    gameState: GameState,
                                    gameSettings: GameSettings,
                                    parts: Seq[String],
                                    messageId: String,
                                   ) extends Reply {
  def toJson: Value = {
    addUserIdActionGameIdMessageId(userId, "get_partial_game_state", gameId, messageId)
    val player =
      if gameSettings.players.nonEmpty then
        gameSettings.players.head.find(_.userId == userId)
      else None
    val house = if player.nonEmpty then player.head.house else None

    val partsJson = if parts.nonEmpty && parts.head == "*" 
      then 
        if house.nonEmpty
        then gameState.toPersonalJson(house.head)
        else gameState.toCleanJson
      else
        val updatedParts =
          if userId > 0
          then parts.filter(_ != GameStateParts.AvailableOrders.string)
          else parts
        gameState.toPartialJson(updatedParts, house)
    json.obj.addOne("gameState" -> partsJson)
  }

}