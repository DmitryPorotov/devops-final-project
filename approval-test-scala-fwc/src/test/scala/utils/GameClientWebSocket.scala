package utils

import org.java_websocket.client.WebSocketClient

import java.net.URI

abstract class GameClientWebSocket(uri: URI) extends WebSocketClient(uri) {

  override def onClose(code: Int, reason: String, remote: Boolean): Unit = {
    print("Closed code " + code)
    println(" Reason " + reason)
  }

  override def onError(ex: Exception): Unit = {
    println(ex.getMessage)
    ex.printStackTrace()
  }
}
