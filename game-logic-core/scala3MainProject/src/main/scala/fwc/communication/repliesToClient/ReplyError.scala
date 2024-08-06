package fwc.communication.repliesToClient
import ujson.Obj

import enrichment.ExtUPickleHashMap


case class ReplyError(
                       userId: Int,
                       gameId: String,
                       message: String,
                       originalMessage: ujson.Value,
                       messageId: String,
                     ) extends Reply {
  def toJson: Obj = {
    addUserIdActionGameIdMessageId(userId, "error", gameId, messageId)
    json.value.addPairs(
      "message" -> message,
      "originalMessage" -> originalMessage,
    )
  }
}
