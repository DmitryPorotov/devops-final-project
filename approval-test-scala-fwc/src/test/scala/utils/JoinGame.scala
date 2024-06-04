package utils

import dto.UserDTO
import ujson.Obj

object JoinGame {

  def getPlayerJoinMessageBuilder(userDTO: UserDTO, joinAs: String, prevMb: Option[MessagesBuilder] = None): MessagesBuilder =
    val mb = prevMb.getOrElse(MessagesBuilder(userDTO.id, 2, None))
    val mb2 = mb.copy(action = None, messageType =  "chat")
    val chatJoinMsgId = mb2.makeId
    val gameJoinMsgId = mb2.addOne(Obj(
      "messageId" -> chatJoinMsgId,
      "body" -> ujson.Obj(
        "type" -> "join"
      ))
    , mb2.lastId.getOrElse("0"))
    val mb3 = mb2.copy(action = Some("join_game"), messageType = "action")
    mb3.addOne(Obj(
      "messageId" -> gameJoinMsgId,
      "action" -> "join_game",
      "joinAs" -> joinAs,
      "name" -> userDTO.name
      )
    , chatJoinMsgId)
    mb3
}
