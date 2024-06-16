package utils

import dto.UserDTO
import ujson.Obj

import java.util.Date

class PlayerInputtingMessagesBuilder(userId: Int,
                                     lobbyId: Int,
                                     action: Option[String] = None,
                                     messageType: String = "chat"
                                    ) extends BaseMessagesBuilder(userId, lobbyId, action, messageType) {

  override def addOne(message: Option[Obj], testRunner: Option[TestRunner], key: String): Unit =
    super.addOne(message, testRunner, key)
    message.foreach(m => {
      if action.nonEmpty then
        m.value.addOne("action" -> action.head)
    })

  override def copy(userId: Int, lobbyId: Int, action: Option[String], messageType: String): PlayerInputtingMessagesBuilder =
    val newMb = PlayerInputtingMessagesBuilder(userId, lobbyId, action, messageType)
    newMb.messages = messages
    newMb._lastId = lastId
    newMb._joinedAs = _joinedAs
    newMb._isHouseTypeSet = _isHouseTypeSet
    newMb._prevTestRunner = _prevTestRunner
    newMb
}


object PlayerInputtingMessagesBuilder {
  private var _user: UserDTO = null
  @volatile private var _isRunningTest = true
  private val _startTimeStamp = new Date().getTime
  def init(): PlayerInputtingMessagesBuilder =
    val user = HttpUtils.login("a@b.com")
    _user = user
    val mb = PlayerInputtingMessagesBuilder(user.id, 2)
    mb.addOne(Some(ujson.Obj(
      "body" -> ujson.Obj(
        "type" -> "create"
      ))
    ))
    val mb2 = mb.copy(action = Some("create_game"), messageType = "action")
    mb2.addOne(Some(ujson.Obj(
      "isRandomHouses" -> false,
      "isInputOnly" -> true,
    )))
    val mb3 = mb2.copy(action = Some("join_game"), messageType = "action")
    mb3.addOne(Some(Obj(
      "action" -> "join_game",
      "joinAs" -> "kraken",
      "name" -> user.name
    )))
    mb3

  def endTest(): Unit =
    _isRunningTest = false
    
  def startTest(messagesBuilder: PlayerInputtingMessagesBuilder): Unit =
    messagesBuilder.addOne()
    val pb = new PlayerBehavior(_user, messagesBuilder.getMap)
    pb.connect()

    while (_isRunningTest)
      val now = new Date().getTime
      if now - _startTimeStamp >= 10000 then
        _isRunningTest = false
      Thread.sleep(1L)
}