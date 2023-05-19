package fwc.communication

import org.zeromq.{ZLoop, ZMQ}
import org.zeromq.ZLoop.IZLoopHandler
import scala.util.Try

object MessageHandlerWrapper {

  def apply(cb : (loop: ZLoop, message: String) => String): IZLoopHandler = {
    (loop: ZLoop, item: ZMQ.PollItem, arg: Any) => {
      val socket = item.getSocket

      val msg = socket.recv()

      try {
        val reply = cb(loop, new String(msg))
        socket.send(reply, 0)
        0
      }
      catch {
        case e: Throwable =>
          socket.send(s"{\"error\":${e.getMessage}}", 0)
          0
      }
    }
  }
}
