package fwc.communication.repliesToClient
import ujson.Obj

case class ReplySaveGame(
                          userId: Int,
                          gameId: String,
                          saveName: String,
                          messageId: String,
                        ) extends Reply {
  def toJson: Obj =
    addUserIdActionGameIdMessageId(userId, "save", gameId, messageId)
    json.value.addOne("saveName" -> saveName)
}
