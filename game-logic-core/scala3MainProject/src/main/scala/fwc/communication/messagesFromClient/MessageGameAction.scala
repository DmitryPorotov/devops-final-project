package fwc.communication.messagesFromClient
import ujson.Obj
import enrichment.ExtUPickleHashMap

case class MessageGameAction(
                              override val userId: Int,
                              override val gameId: String,
                              gameAction: ujson.Value,
                              override val messageId: String,
                            ) extends Message(userId: Int, gameId: String, messageId: String) {
  override def toJson: Obj = 
    super.toJson.value.addPairs(
      "action" -> "game_action",
      "player_action" -> gameAction
    )
}
