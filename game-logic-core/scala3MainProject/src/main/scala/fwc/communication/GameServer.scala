package fwc.communication
import fwc.game.FWCException

import scala.util.{Failure, Success, Try}
import redis.clients.jedis.*

object GameServer {

  private val redisHost: String =
    if System.getenv("REDIS_HOST") == null
    then "localhost"
    else System.getenv("REDIS_HOST")

  private val workerName: String =
    if System.getenv("WORKER_NAME") == null
    then "worker1"
    else System.getenv("WORKER_NAME")

  private val jedisSub = new Jedis(redisHost, 6379)
  private val jedisPub = new Jedis(redisHost, 6379)
  private val jedisCache = new Jedis(redisHost, 6379)
  private val subscriber = new JedisPubSub {
    override def onPMessage(pattern: String, channel: String, message: String): Unit = {
      val replyTo = channel.split('.')(1)
      println(" [x] Received '" + message + "'")
      val reply = Try[String](Reactor.apply(message)) match
        case Success(s) => s
        case Failure(e: FWCException) => ujson.Obj(
            "action" -> "error",
            "message" -> e.getMessage
          )
          .render(fwc.jsonIndentation)
        case Failure(e) => ujson.Obj(
            "action" -> "error",
            "message" -> e.getMessage,
            "trace" -> ujson.Arr.from(e.getStackTrace.map(_.toString))
          )
          .render(fwc.jsonIndentation)
      println(" [x] Sent '" + (if reply.length > 1000 then reply.substring(0, 1000) else reply) + "'")
      jedisPub.publish(replyTo + "." + workerName, reply)
    }
  }
  def start(): Unit = {
    jedisSub.psubscribe(subscriber, workerName + ".*", "new_game.*")
  }
  def shutdown(): Unit =
    subscriber.unsubscribe()
    val ids = Reactor.prepareShutdown.foldLeft(Map[String, ujson.Str]())((map, kv) => {
      jedisCache.set("game_" + kv._2.gameSettings.gameUuid.toString, kv._2.toFullJson.render())
      map + (kv._1 -> kv._2.gameSettings.gameUuid.toString)
    })
    jedisCache.set(s"${workerName}_games", ujson.Obj.from(ids).render())
    jedisPub.publish(s"servers.$workerName", "shutting-down")
}
