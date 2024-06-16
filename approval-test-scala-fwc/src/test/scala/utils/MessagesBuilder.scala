package utils

import ujson.Obj

case class MessagesBuilder(
                            userId: Int,
                            lobbyId: Int,
                            action: Option[String] = None,
                            messageType: String = "chat"
                          ) extends BaseMessagesBuilder(userId, lobbyId, action, messageType) {

  override def addOne(message: Option[Obj], testRunner: Option[TestRunner], key: String): Unit =
    super.addOne(message, testRunner, key)
    message.foreach(m => {
      addHouseType(m)
    })

  def copy(userId: Int = this.userId, lobbyId: Int = this.lobbyId, action: Option[String] = this.action, messageType: String = this.messageType): MessagesBuilder =
    val newMb = MessagesBuilder(userId, lobbyId, action, messageType)
    newMb.messages = messages
    newMb._lastId = lastId
    newMb._joinedAs = _joinedAs
    newMb._isHouseTypeSet = _isHouseTypeSet
    newMb._prevTestRunner = _prevTestRunner
    newMb
}
