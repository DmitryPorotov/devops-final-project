package utils

import dto.UserDTO
import org.java_websocket.handshake.ServerHandshake

import java.net.URI

class PlayerBehavior private {

  private val baseUri = "ws://localhost:8888/ws?_token="
  private final var user: UserDTO = null
  private final var socket: GameClientWebSocket = null
  private final var messages: Map[String,ujson.Obj] = null
  private final val self = this
  def this(user: UserDTO, messages: Map[String, ujson.Obj]) = {
    this()
    this.user = user
    this.messages = messages
    this.socket = new GameClientWebSocket(URI.create(baseUri + user.token)):
      override def onOpen(handshakedata: ServerHandshake): Unit =
        self.onOpen(handshakedata)
      override def onMessage(message: String): Unit =
        self.onMessage(message)
  }

  def onOpen(handshakedata: ServerHandshake): Unit =
    socket.send(messages("0").render())

  def onMessage(message: String): ujson.Obj =
    val j = ujson.read(message)
    if j.obj.getOrElse("action", ujson.Str("")).str.equals("error") then
      println(j.obj("message").str)
    else
      val msgId = j.obj("messageId").str
      val msg = messages.getOrElse(msgId, null)
      if msg != null then
        socket.send(msg.render())
    j.obj

  def connect(): Unit = {
    this.socket.connect()
  }
}
