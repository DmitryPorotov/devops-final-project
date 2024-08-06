package fwc.communication.repliesToClient

case class ReplyTestConnectivity(messageId: String) extends Reply {
  def toJson: ujson.Obj = {
    json.value.addOne("action" -> "hello")
  }
}
