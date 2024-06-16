package utils

import dto.UserDTO
import ujson.Obj
import scala.collection.mutable.{Map => MutMap}

object JoinGame {

  def getPlayerJoinMessageBuilder(userDTO: UserDTO, joinAs: String, prevMb: Option[BaseMessagesBuilder] = None): BaseMessagesBuilder =
    val mb = prevMb.getOrElse(MessagesBuilder(userDTO.id, 2))
    val mb2 = mb.copy(action = None, messageType = "chat")
    mb2.addOne(Some(Obj(
      "body" -> ujson.Obj(
        "type" -> "join"
      ))))
    val mb3 = mb2.copy(action = Some("join_game"), messageType = "action")
    mb3.addOne(Some(Obj(
      "action" -> "join_game",
      "joinAs" -> joinAs,
      "name" -> userDTO.name
    )))
    mb3

  def allLogin(): Map[Int, UserDTO] =
    val owner1 = HttpUtils.login("a@b.com")
    val user2 = HttpUtils.login("b@b.com")
    val user3 = HttpUtils.login("admin@b.com")
    val user4 = HttpUtils.login("c@b.com")
    val user5 = HttpUtils.login("d@b.com")
    val user6 = HttpUtils.login("e@b.com")
    Map(1 -> owner1, 2 -> user2, 3 -> user3, 4 -> user4, 5 -> user5, 6 -> user6)

  def getAllUsersAndMessageBuilders(singleThreaded: Boolean = false): (Map[Int, UserDTO], MutMap[Int, BaseMessagesBuilder]) =
    val users = allLogin()
    val o1Mb = CreateGame.getCreateGameOwnerMessagesBuilder(users(1), if singleThreaded then Some(SingleThreadedMessagesBuilder(users(1).id, 2)) else None)
    val o1MbUpdated = JoinGame.getPlayerJoinMessageBuilder(users(1), "kraken", Some(o1Mb))
    val u2Mb = JoinGame.getPlayerJoinMessageBuilder(users(2), "lion", if singleThreaded then Some(SingleThreadedMessagesBuilder(users(2).id, 2)) else None)
    val u3Mb = JoinGame.getPlayerJoinMessageBuilder(users(3), "moose", if singleThreaded then Some(SingleThreadedMessagesBuilder(users(3).id, 2)) else None)
    val u4Mb = JoinGame.getPlayerJoinMessageBuilder(users(4), "pufferfish", if singleThreaded then Some(SingleThreadedMessagesBuilder(users(4).id, 2)) else None)
    val u5Mb = JoinGame.getPlayerJoinMessageBuilder(users(5), "rose", if singleThreaded then Some(SingleThreadedMessagesBuilder(users(5).id, 2)) else None)
    val u6Mb = JoinGame.getPlayerJoinMessageBuilder(users(6), "wolf", if singleThreaded then Some(SingleThreadedMessagesBuilder(users(6).id, 2)) else None)

    (
      users,
      MutMap(1 -> o1MbUpdated, 2 -> u2Mb, 3 -> u3Mb, 4 -> u4Mb, 5 -> u5Mb, 6 -> u6Mb)
    )
    
}
