package fwc.communication.repliesToClient

import fwc.JsonSerializable

trait Reply extends JsonSerializable {
  protected val json: ujson.Obj = ujson.Obj("type" -> "action")

  protected def addActionGameIdMessageId(action: String, gameId: String, messageId: String): Unit = {
    json.value.addAll(Map(
      "action" -> action,
      "gameId" -> gameId,
      "messageId" -> messageIdToJsonType(messageId),
    ))
  }

  private def messageIdToJsonType(messageId: String): ujson.Value = if messageId != null then messageId else ujson.Null

  protected def addUserIdActionGameIdMessageId(userId: Int, action: String, gameId: String, messageId: String): Unit = {
    addActionGameIdMessageId(action, gameId, messageId)
    json.value.addOne("userId" -> userId)
  }

}
