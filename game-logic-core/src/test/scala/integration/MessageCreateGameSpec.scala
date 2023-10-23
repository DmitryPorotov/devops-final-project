package integration

import com.rabbitmq.client.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.util.{Try, Using}
//import org.zeromq.ZMQ
//import org.zeromq.ZMQ.{Context, Socket}
import java.util.UUID
class MessageCreateGameSpec extends AnyFlatSpec with should.Matchers {

  private val TO_WORKERS_EXCHANGE = "to_workers"
  private val FROM_WORKERS_EXCHANGE = "from_workers"

  "The server" should "return the game id" in {
    val factory = new ConnectionFactory()
    factory.setHost("localhost")


    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()
    val message = "{\"userId\":1,\"action\":\"create_game\"}"
    channel.exchangeDeclare(TO_WORKERS_EXCHANGE, "topic")
    channel.exchangeDeclare(FROM_WORKERS_EXCHANGE, "topic")
    val queueName = channel.queueDeclare.getQueue
    channel.queueBind(queueName, FROM_WORKERS_EXCHANGE, "server1.*")
    channel.basicPublish(TO_WORKERS_EXCHANGE, "worker1.server1", null, message.getBytes("UTF-8"))
    println(" [x] Sent '" + message + "'")
    channel.basicConsume(queueName, true, (consumerTag, delivery: Delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(" [x] Received '" + message + "'")
      val json = ujson.read(message)
      val gameId = Try[String](json.obj("gameId").str) getOrElse null
      assert(gameId != null, "New game should have an id")
    }, consumerTag => {})

    Thread.sleep(1000)
    //    val context = ZMQ.context(1)
    //    val socket = context.socket(ZMQ.REQ)
    //    socket.connect("tcp://127.0.0.1:5555")
    //    socket.send("{\"userId\":1,\"action\":\"create_game\"}", 0)
    //    val reply = new String(socket.recv(0))
    //    val json = ujson.read(reply)
  }
}
