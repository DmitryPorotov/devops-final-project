package fwc.communication

import org.zeromq.{ZLoop, ZMQ}
import org.zeromq.ZMQ.{Context, PollItem, Poller, Socket}

object GameServer {

  def start(): Unit = {
    val context = ZMQ.context(1)

    val socket = context.socket(ZMQ.REP)
    socket.bind("tcp://0.0.0.0:5555")

    val poller = new PollItem(socket, 7)

    val loop = new ZLoop()
    loop.addPoller(poller, MessageHandlerWrapper(Reactor.apply), null)
    loop.start()
  }
}
