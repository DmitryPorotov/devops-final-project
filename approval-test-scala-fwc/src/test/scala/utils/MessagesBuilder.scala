package utils

import scala.collection.mutable.Map as MutMap
import java.util.UUID

case class MessagesBuilder(
                            userId: Int,
                            lobbyId: Int,
                            action: Option[String],
                            messageType: String = "chat"
                          ) {


  private var messages: MutMap[String, ujson.Obj] = MutMap()

  private var _lastId: Option[String] = None

  def lastId: Option[String] = _lastId

  def addOne(message: ujson.Obj, key: String = "0"): String =
    _lastId = Some(message("messageId").str)
    message.value.addOne("userId" -> userId)
    message.value.addOne("lobbyId" -> lobbyId)
    message.value.addOne("type" -> messageType)
    if action.nonEmpty then
      message.value.addOne("action" -> action.head)
    messages.addOne(key -> message)
    makeId

  def getMap: Map[String, ujson.Obj] =
    messages.toMap

  def makeId: String = UUID.randomUUID().toString

  def copy(userId: Int = this.userId, lobbyId: Int = this.lobbyId, action: Option[String] = this.action, messageType: String = this.messageType): MessagesBuilder =
    val newMb = MessagesBuilder(userId, lobbyId, action, messageType)
    newMb.messages = messages
    newMb._lastId = lastId
    newMb
}
