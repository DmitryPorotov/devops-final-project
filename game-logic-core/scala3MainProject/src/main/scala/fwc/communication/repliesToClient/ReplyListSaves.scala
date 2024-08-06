package fwc.communication.repliesToClient
import ujson.Obj

case class ReplyListSaves(
                           userId: Int,
                           gameId: String,
                           saves: Seq[String],
                           messageId: String,
                         ) extends Reply {
  def toJson: Obj =
    addUserIdActionGameIdMessageId(userId, "list_saves", gameId, messageId)
    json.value.addOne("saves" -> saves)
}
