
object FunWithChairs extends App {
//  try {
    Runtime.getRuntime.addShutdownHook(new Thread {
        override def run(): Unit =
            println("In the shutdown hook.")
            GameServer.shutdown()
    })
    GameServer.start()
//  }
//  catch
//    case e: Throwable =>
//      println(e.toString)
//      println(e.getMessage)
//      println(e.printStackTrace())
//      throw e
}
