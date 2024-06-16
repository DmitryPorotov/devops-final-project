package utils

import java.util.UUID
import scala.collection.mutable.Map as MutMap

abstract class BaseMessagesBuilder(
                           userId: Int,
                           lobbyId: Int,
                           action: Option[String] = None,
                           messageType: String = "chat"
                         )  {
  protected var messages: MutMap[String, (Option[ujson.Obj], Option[TestRunner])] = MutMap()

  protected var _lastId: String = "0"

  protected var _joinedAs: String = ""

  protected var _isHouseTypeSet: Boolean = false

  protected var _prevTestRunner: Option[TestRunner] = None

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
    })

    messages.addOne(key -> (message, testRunnerToAdd))

  protected def addHouseType(message: ujson.Obj): Unit =
    if _isHouseTypeSet && action.head == "game_action" then
      message.obj("player_action").obj.addOne("houseType" -> _joinedAs)
  
  def getMap: Map[String, (Option[ujson.Obj], Option[TestRunner])] =
    messages.toMap

  def makeId: String = UUID.randomUUID().toString

  def copy(userId: Int = this.userId, lobbyId: Int = this.lobbyId, action: Option[String] = this.action, messageType: String = this.messageType): BaseMessagesBuilder 
}
