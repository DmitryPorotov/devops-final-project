package fwc.communication.repliesToClient

import fwc.GameSettings

case class ReplyJoinGame(
                          userId: Int,
                          gameId: String,
                          gameSettings: GameSettings,
                          messageId: String,
                        ) extends Reply {
  def toJson: ujson.Obj = {
    addUserIdActionGameIdMessageId(userId, "join_game", gameId, messageId)
    json.value.addOne("gameSettings" -> gameSettings.toJson)
  }
}
