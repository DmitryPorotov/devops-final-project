import fwc.communication.GameServer
import Scala2Project.MainScala2Project

object Main extends App {
  MainScala2Project.doNothing
  println("Starting worker...")
  GameServer.start()
}