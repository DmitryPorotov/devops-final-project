package utils

import scala.collection.mutable.Map as MutMap
import java.util.UUID

case class MessagesBuilder(
                            userId: Int,
                            lobbyId: Int,
                            action: Option[String],
                            messageType: String = "chat"
                          ) {


  private var messages: MutMap[String, (Option[ujson.Obj], Option[TestRunner])] = MutMap()

  private var _lastId: String = "0"

  private var _joinedAs: String = ""

  private var _isHouseTypeSet: Boolean = false

  private var _prevTestRunner: Option[TestRunner] = None

  def lastId: String = _lastId

  def addOne(message: Option[ujson.Obj] = None, testRunner: Option[TestRunner] = None, key: String = _lastId): Unit =
    val testRunnerToAdd = _prevTestRunner
    _prevTestRunner = testRunner
    _lastId = makeId
    message.foreach(m => {
      m.value.addOne("messageId" -> _lastId)
      m.value.addOne("userId" -> userId)
      m.value.addOne("lobbyId" -> lobbyId)
      m.value.addOne("type" -> messageType)
      if action.nonEmpty then
        m.value.addOne("action" -> action.head)
        if !_isHouseTypeSet && action.head == "join_game" then
          _isHouseTypeSet = true
          _joinedAs = m.obj("joinAs").str
        else if action.head == "game_action" then
          m.obj("player_action").obj.addOne("houseType" -> _joinedAs)
    })

    messages.addOne(key -> (message, testRunnerToAdd))

  def getMap: Map[String, (Option[ujson.Obj], Option[TestRunner])] =
    messages.toMap

  def makeId: String = UUID.randomUUID().toString

  def copy(userId: Int = this.userId, lobbyId: Int = this.lobbyId, action: Option[String] = this.action, messageType: String = this.messageType): MessagesBuilder =
    val newMb = MessagesBuilder(userId, lobbyId, action, messageType)
    newMb.messages = messages
    newMb._lastId = lastId
    newMb._joinedAs = _joinedAs
    newMb._isHouseTypeSet = _isHouseTypeSet
    newMb._prevTestRunner = _prevTestRunner
    newMb
}
