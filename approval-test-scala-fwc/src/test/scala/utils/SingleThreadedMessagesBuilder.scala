package utils
import dto.UserDTO
import ujson.Obj

import java.util.Date
import scala.annotation.tailrec
import scala.collection.mutable.Map as MutMap

case class SingleThreadedMessagesBuilder(
                                          userId: Int,
                                          lobbyId: Int,
                                          action: Option[String] = None,
                                          messageType: String = "chat")
extends BaseMessagesBuilder(userId, lobbyId, action, messageType) {

  override def addOne(
                       message: Option[Obj] = None,
                       testRunner: Option[TestRunner] = None,
                       key: String = SingleThreadedMessagesBuilder._sharedLastMessageId
                     ): Unit =
    super.addOne(message, testRunner, key)
    val testRunnerToAdd = SingleThreadedMessagesBuilder._sharedPrevTestRunner
    SingleThreadedMessagesBuilder._sharedPrevTestRunner = testRunner
    SingleThreadedMessagesBuilder._sharedLastMessageId = makeId
    message.foreach(m => {
      m.obj("messageId") = SingleThreadedMessagesBuilder._sharedLastMessageId
      addHouseType(m)
    })
    SingleThreadedMessagesBuilder.messages.addOne(key -> (message, testRunnerToAdd))

  def copy(userId: Int = this.userId, lobbyId: Int = this.lobbyId, action: Option[String] = this.action, messageType: String = this.messageType): SingleThreadedMessagesBuilder =
    val newMb = SingleThreadedMessagesBuilder(userId, lobbyId, action, messageType)
    newMb.messages = messages
    newMb._lastId = lastId
    newMb._joinedAs = _joinedAs
    newMb._isHouseTypeSet = _isHouseTypeSet
    newMb._prevTestRunner = _prevTestRunner
    newMb
}

object SingleThreadedMessagesBuilder {
  private var _sharedLastMessageId: String = "0"
  private var _sharedPrevTestRunner: Option[TestRunner] = None
  private val messages: MutMap[String, (Option[ujson.Obj], Option[TestRunner])] = MutMap()
  private var _user: UserDTO = null
  @volatile private var _isRunningTest = true
  private val _startTimeStamp = new Date().getTime

  def getMap: Map[String, (Option[ujson.Obj], Option[TestRunner])] =
    messages.toMap

  def init(): MutMap[Int, BaseMessagesBuilder] =
    val (users, messageBuilders) = JoinGame.getAllUsersAndMessageBuilders(true)
    _user = users(1)
    messageBuilders

  def endTest(): Unit =
    _isRunningTest = false

  def startTest(): Unit =
    val u1Pb = new PlayerBehavior(_user, SingleThreadedMessagesBuilder.getMap)
    u1Pb.connect()
    while (_isRunningTest)
      val now = new Date().getTime
      if now - _startTimeStamp >= 10000 then
        _isRunningTest = false
      Thread.sleep(1L)

  def testMessages(): Boolean =
    val numMessages = messages.size
    @tailrec
    def getNextMessage(id: String, num: Int = 1): Boolean =
      val msg = messages.getOrElse(id, null)
      if msg != null && msg._1.nonEmpty then
        getNextMessage(msg._1.head.obj("messageId").str, num + 1)
      else num == numMessages
    getNextMessage("0")

}
