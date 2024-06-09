package utils
import dto.UserDTO
import org.java_websocket.handshake.ServerHandshake

import java.net.URI
import java.util.UUID

object CreateGame {

  private final val uri = "ws://localhost:8888/ws?_token="

  def createGame(): Unit =

    val user = HttpUtils.login("a@b.com")
    val user2 = HttpUtils.login("b@b.com")

    val con = new GameClientWebSocket(URI.create(uri + user.token)):
      override def onOpen(handshakedata: ServerHandshake): Unit =
        val chatCreateMsgId = "12343243242"
        send(ujson.Obj(
          "type" -> "chat",
          "userId" -> user.id,
          "messageId" -> chatCreateMsgId,
          "lobbyId" -> 2,
          "body" -> ujson.Obj {
            "type" -> "create"
          }
        ).render())

      override def onMessage(message: String): Unit =
        val gameCreateMsgId = "dsfsdfsdfsd"
        val json = ujson.read(message)
        if json.obj("messageId").str.equals("12343243242") then
          send(ujson.Obj(
            "type" -> "action",
            "userId"-> user.id,
            "messageId"-> gameCreateMsgId,
            "lobbyId"-> 2,
            "action"-> "create_game",
            "isRandomHouses"-> false,
          ).render())

    con.connect()
    val con2 = new GameClientWebSocket(URI(uri + user2.token)):
      override def onOpen(handshakedata: ServerHandshake): Unit =
        send(ujson.Obj(
            "type"->"action",
            "userId"-> user2.id,
            "lobbyId"-> 2,
            "action"-> "join_game",
            "messageId"-> "dsafdsfsdfsdfsdfewgghf",
            "joinAs" -> "wolf",
            "name"-> user2.name
        ).render())

      override def onMessage(message: String): Unit =
        println(message)

    con2.connect()
    Thread.sleep(1000_000_000L)

  def getCreateGameOwnerMessagesBuilder(userDTO: UserDTO): MessagesBuilder =
    val mb = MessagesBuilder(userDTO.id, 2, None)
    mb.addOne(Some(ujson.Obj(
      "body" -> ujson.Obj(
        "type" -> "create"
      ))
    ))
    val mb2 = mb.copy(action = Some("create_game"), messageType = "action")
    mb2.addOne(Some(ujson.Obj(
      "isRandomHouses" -> false
    )))
    mb2

}
