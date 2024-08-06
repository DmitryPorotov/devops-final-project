package fwc.communication.repliesToClient

case class ReplyCreateGame(
                            gameId: String,
                            messageId: String,
                          ) extends Reply {
  def toJson: ujson.Obj = {
    addActionGameIdMessageId("create_game", gameId, messageId)
    json
  }
}
