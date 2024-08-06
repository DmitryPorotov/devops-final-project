package fwc.communication.repliesToClient
import ujson.Obj

case class ReplyLoadGame(
                          gameId: String,
                          messageId: String,
                        ) extends Reply {

  override def toJson: Obj = {
    addActionGameIdMessageId("load", gameId, messageId)
    json
  }
}
