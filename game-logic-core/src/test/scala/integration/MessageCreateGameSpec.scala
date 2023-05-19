package integration

import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.zeromq.ZMQ
import org.zeromq.ZMQ.{Context, Socket}

class MessageCreateGameSpec extends AnyFlatSpec with should.Matchers {
  "The server" should "return the game id" in {
    val context = ZMQ.context(1)
    val socket = context.socket(ZMQ.REQ)
    socket.connect("tcp://127.0.0.1:5555")
    socket.send("{\"userId\":1,\"action\":\"create_game\"}", 0)
    val reply = new String(socket.recv(0))
    val json = ujson.read(reply)
  }
}
