package fwc.communication.repliesToClient

import fwc.gameSaving.GameReplay

case class ReplyGetStatus(
                           userId: Int,
                           gameId: String,
                           gameReplay: GameReplay,
                           messageId: String,
                         ) extends Reply {
  def toJson: ujson.Obj = {
    addUserIdActionGameIdMessageId(userId ,"get_status", gameId, messageId)
    val status = gameReplay != null
    val details =
      if status
      then StatusDetails(gameReplay).toJson
      else ujson.Null

    json.value.addOne("status" -> GameStatus(status, details).toJson)
  }
}
