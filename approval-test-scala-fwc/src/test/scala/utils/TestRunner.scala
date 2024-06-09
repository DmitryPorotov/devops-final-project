package utils

trait TestRunner {
  def onMessage(jsonMessage: ujson.Obj): Unit
}
