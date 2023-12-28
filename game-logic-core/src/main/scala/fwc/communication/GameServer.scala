package fwc.communication
//import com.rabbitmq.client.*
import fwc.communication.messages.Message
import fwc.game.FWCException

import scala.util.{Failure, Success, Try}
import redis.clients.jedis.*

//import org.zeromq.{ZLoop, ZMQ}
//import org.zeromq.ZMQ.{Context, PollItem, Poller, Socket}

object GameServer {

//  private val TO_WORKERS_EXCHANGE = "to_workers"
//  private val FROM_WORKERS_EXCHANGE = "from_workers"
  def start(): Unit = {
    val redisHost =
      if System.getenv("REDIS_HOST") == null
      then "localhost"
      else System.getenv("REDIS_HOST")

    val workerName =
      if System.getenv("WORKER_NAME") == null
      then "worker1"
      else System.getenv("WORKER_NAME")

    val jedisSub = new Jedis(redisHost, 6379)
    val jedisPub = new Jedis(redisHost, 6379)
    jedisSub.psubscribe(new JedisPubSub {
      override def onPMessage(pattern: String, channel: String, message: String): Unit = {
        val replyTo = channel.split('.')(1)
        println(" [x] Received '" + message + "'")
        val reply = Try[String](Reactor.apply(message)) match
          case Success(s) => s
          case Failure(e: FWCException) => ujson.Obj(
              "error" -> "error",
              "message" -> e.getMessage
            )
            .render(fwc.jsonIndentation)
          case Failure(e) => ujson.Obj(
              "error" -> "error",
              "message" -> e.getMessage,
              "trace" -> ujson.Arr.from(e.getStackTrace.map(_.toString))
            )
            .render(fwc.jsonIndentation)
        println(" [x] Sent '" + (if reply.length > 1000 then reply.substring(0, 1000) else reply) + "'")
        jedisPub.publish(replyTo + "." + workerName, reply)
      }
    }, workerName + ".*", "new_game.*")

//    val rabbitHost =
//      if System.getenv("RABBIT_HOST") == null
//      then "localhost"
//      else System.getenv("RABBIT_HOST")
//
//    val factory = new ConnectionFactory()
//    factory.setHost(rabbitHost)
//    println("Trying to connect to " + factory.getHost + ":" + factory.getPort)
//    val connection: Connection = factory.newConnection()
//    val channel: Channel = connection.createChannel()
//
//    channel.exchangeDeclare(TO_WORKERS_EXCHANGE, "topic")
//    channel.exchangeDeclare(FROM_WORKERS_EXCHANGE, "topic")
//    val queueName = channel.queueDeclare.getQueue
//    channel.queueBind(queueName, TO_WORKERS_EXCHANGE, "new_game.*")
//    channel.queueBind(queueName, TO_WORKERS_EXCHANGE, "worker1.*")
//
//    channel.basicConsume(queueName, true, (consumerTag, delivery: Delivery) => {
//      val message = new String(delivery.getBody, "UTF-8")
//      println(" [x] Received '" + message + "'")
//      val reply = Try[String](Reactor.apply(message)) match
//        case Success(s) => s
//        case Failure(e) => ujson.Obj("error" -> "error", "message" -> e.getMessage)
//          .render(fwc.jsonIndentation)
//      println(" [x] Sent '" + reply + "'")
//      val serverName = delivery.getEnvelope.getRoutingKey.split('.')(1)
//      channel.basicPublish(FROM_WORKERS_EXCHANGE, serverName + ".worker1", null, reply.getBytes("UTF-8"))
//    }, consumerTag => {})

    //    val context = ZMQ.context(1)
//
//    val socket = context.socket(ZMQ.REP)
//    socket.bind("tcp://0.0.0.0:5555")
//
//    val poller = new PollItem(socket, 7)
//
//    val loop = new ZLoop()
//    loop.addPoller(poller, MessageHandlerWrapper(Reactor.apply), null)
//    loop.start()
  }
}
