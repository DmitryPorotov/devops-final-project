import fwc.communication.{Reactor, RestoreGamesException}
import fwc.game.FWCException
import fwc.communication.messagesFromClient.Message

import scala.util.{Failure, Success, Try}
import redis.clients.jedis.*
import redis.clients.jedis.exceptions.JedisConnectionException

import scala.annotation.tailrec

object GameServer {

  private val redisHost: String =
    if System.getenv("REDIS_HOST") == null
    then "localhost"
    else System.getenv("REDIS_HOST")

  private val workerName: String =
    if System.getenv("WORKER_NAME") == null
    then "worker1"
    else System.getenv("WORKER_NAME")
    
  private val isProd: Boolean = "prod".equals(System.getenv("ENVIRONMENT"))

  private val jedisSub = new Jedis(redisHost, 6379)
  private val jedisPub = new Jedis(redisHost, 6379)
  private val jedisCache = new Jedis(redisHost, 6379)
  private val subscriber = new JedisPubSub {
    override def onPMessage(pattern: String, channel: String, message: String): Unit = {
      if isShuttingDown then return 
      val replyTo = channel.split('.')(1) + "." + workerName
      if !isProd then println(" [x] Received from " + channel + "\n '" + message + "'")
      val (reply, needsCropping) =
        try {
          Try[(Message, ujson.Value)](Message.parse(message)) match
            case Failure(e: FWCException) =>
              (ujson.Obj(
                "action" -> "error",
                "message" -> e.getMessage,
                "type" -> "action",
                "gameId" -> (if e.gameId != null then e.gameId else ujson.Null),
                "userId" -> e.userId,
                "messageId" -> e.messageId,
              ).render(fwc.jsonIndentation), true)
            case Failure(e) => throw e
            case Success((msg: Message, json: ujson.Value)) =>
              Try[ujson.Value](Reactor(msg, json)) match
                case Success(j) =>
                  val doCrop = j.obj("action").str.equals("get_game_state")
                  (j.render(fwc.jsonIndentation), doCrop)
                case Failure(e: FWCException) =>
                  val errJson = ujson.Obj(
                    "action" -> "error",
                    "message" -> e.getMessage,
                    "type" -> "action",
                    "messageId" -> e.messageId,
                  )
                  if e.gameId != null then
                    errJson.value.addOne("gameId" -> e.gameId)
                  if e.userId != -1 then
                    errJson.value.addOne("userId" -> e.userId)
                  (errJson.render(fwc.jsonIndentation), false)
                case Failure(e: RestoreGamesException) =>
                  e.games.foreach(gId => {
                    val gameReplayStr = jedisCache.get("game_save_" + gId)
                    jedisCache.del("game_save_" + gId)
                    Reactor.restoreGame(gameReplayStr)
                  })
                  (ujson.Obj(
                    "action" -> "restore_games",
                    "messageId" -> e.messageId
                  ).render(fwc.jsonIndentation), false)
                case Failure(e) => throw e
        }
        catch
          case e: Exception =>
            val a = 0
            (ujson.Obj(
                "action" -> "error",
                "message" -> e.getMessage,
                "type" -> "action",
                "trace" -> ujson.Arr.from(e.getStackTrace.map(_.toString)),
              )
              .render(fwc.jsonIndentation), true)
      if !isProd then println(" [x] Sent to " + replyTo + "\n '" + (
        if reply.length > 500 && needsCropping
        then reply.substring(0, 500)
        else reply
        ) + "'")
      if !isShuttingDown then 
        jedisPub.publish(replyTo, reply)
    }
  }
  
  @volatile 
  private var isShuttingDown: Boolean = false
  
  @tailrec
  def start(retry: Int = 0): Unit = {
    try {
      jedisSub.psubscribe(subscriber, workerName + ".*", "new_game.*")
    } catch
      case e: JedisConnectionException =>
        if retry <= 100 then
          val delay = Math.E
          println(s"Could not connect to Redis. Waiting $delay seconds to retry. ${if retry > 0 then s" Retry #$retry" else ""}")
          Thread.sleep((delay * 1000).toLong)
          start(retry + 1)
  }
  
  def shutdown(): Unit =
    isShuttingDown = true
    if subscriber.isSubscribed then
      subscriber.unsubscribe()
    val ids = Reactor.prepareShutdown.foldLeft(Map[String, ujson.Str]())((map, kv) => {
      jedisCache.set("game_save_" + kv._2.gameSettings.gameUuid.toString, kv._2.toJson.render())
      map + (kv._1 -> kv._2.gameSettings.gameUuid.toString)
    })
    if ids.nonEmpty then
      jedisCache.lpush(s"${workerName}_games", ujson.Obj.from(ids).render())
      jedisPub.publish(s"servers.$workerName", "{\"action\":\"shutdown\"}")
    jedisPub.close()
    jedisSub.close()
    jedisCache.close()
    
}
