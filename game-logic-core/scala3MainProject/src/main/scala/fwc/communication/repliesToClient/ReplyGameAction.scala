package fwc.communication.repliesToClient

case class ReplyGameAction(
                           gameId: String,
                           reply: ujson.Value,
                           messageId: String,
                          ) extends Reply {
  def toJson: ujson.Obj =
    addActionGameIdMessageId("game_action", gameId, messageId)
    json.value.addOne("reply" -> reply)
}
