package fwc.communication.repliesToClient

case class ReplyStartGame(
                           gameId: String,
                           messageId: String,
                         ) extends Reply {
  def toJson: ujson.Obj = {
    addActionGameIdMessageId("start_game", gameId, messageId)
    json
  }
}
