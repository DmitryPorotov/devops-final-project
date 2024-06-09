package utils

import dto.UserDTO
import ujson.Obj
import scala.collection.mutable.{Map => MutMap}

object JoinGame {

  def getPlayerJoinMessageBuilder(userDTO: UserDTO, joinAs: String, prevMb: Option[MessagesBuilder] = None): MessagesBuilder =
    val mb = prevMb.getOrElse(MessagesBuilder(userDTO.id, 2, None))
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

  def getAllUsersAndMessageBuilders: (Map[Int, UserDTO], MutMap[Int, MessagesBuilder]) =
    val owner1 = HttpUtils.login("a@b.com")
    val user2 = HttpUtils.login("b@b.com")
    val user3 = HttpUtils.login("admin@b.com")
    val user4 = HttpUtils.login("c@b.com")
    val user5 = HttpUtils.login("d@b.com")
    val user6 = HttpUtils.login("e@b.com")
    val o1Mb = CreateGame.getCreateGameOwnerMessagesBuilder(owner1)
    val o1MbUpdated = JoinGame.getPlayerJoinMessageBuilder(owner1, "kraken", Some(o1Mb))
    val u2Mb = JoinGame.getPlayerJoinMessageBuilder(user2, "lion")
    val u3Mb = JoinGame.getPlayerJoinMessageBuilder(user3, "moose")
    val u4Mb = JoinGame.getPlayerJoinMessageBuilder(user4, "pufferfish")
    val u5Mb = JoinGame.getPlayerJoinMessageBuilder(user5, "rose")
    val u6Mb = JoinGame.getPlayerJoinMessageBuilder(user6, "wolf")

    (
      Map(1 -> owner1, 2 -> user2, 3 -> user3, 4 -> user4, 5 -> user5, 6 -> user6),
      MutMap(1 -> o1MbUpdated, 2 -> u2Mb, 3 -> u3Mb, 4 -> u4Mb, 5 -> u5Mb, 6 -> u6Mb)
    )
}
