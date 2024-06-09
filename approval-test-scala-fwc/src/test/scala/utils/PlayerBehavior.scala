package utils

import dto.UserDTO
import org.java_websocket.handshake.ServerHandshake

import java.net.URI

class PlayerBehavior private {

  private val baseUri = "ws://localhost:8888/ws?_token="
  private final var user: UserDTO = null
  private final var socket: GameClientWebSocket = null
  private final var messages: Map[String, (Option[ujson.Obj], Option[TestRunner])] = null
  private final val self = this
  def this(user: UserDTO, messages: Map[String, (Option[ujson.Obj], Option[TestRunner])]) = {
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
    messages("0")._1.foreach(m => socket.send(m.render()))

  def onMessage(message: String): Unit =
    val j = ujson.read(message)
    if j.obj.getOrElse("action", ujson.Str("")).str.equals("error") then
      println(j.obj("message").str)
    else if j.obj("type").str.equals("error") then
      println(j.obj("body").obj("body").str)
    else
      val msgId = j.obj("messageId").str
      val msg = messages.getOrElse(msgId, null)
      if msg != null then
        msg._2.foreach(_.onMessage(j.obj))
        msg._1.foreach(m => socket.send(m.render()))
      else
        val defTest = messages.getOrElse("default", null)
        if defTest != null then
          defTest._2.foreach(_.onMessage(j.obj))


  def connect(): Unit = {
    this.socket.connect()
  }
}
