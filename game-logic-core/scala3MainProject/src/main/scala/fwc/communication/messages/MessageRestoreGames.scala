package fwc.communication.messages

case class MessageRestoreGames(
                               override val userId: Int,
                               override val gameId: String,
                               override val messageId: String,
                               games: List[String],
                             )
                              extends Message(userId: Int, gameId: String, messageId: String)