package fwc.communication.repliesToClient
import ujson.Obj

case class ReplyNewGame(
                         gameId: String,
                         gamesCount: Int,
                         messageId: String,
                       ) extends Reply {
  def toJson: Obj = {
    addActionGameIdMessageId("new_game", gameId, messageId)
    json.value.addOne("gamesCount" -> gamesCount)
  }
}
