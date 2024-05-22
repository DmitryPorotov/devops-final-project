package fwc.communication.messages

case class MessageStartGame(
                             override val userId: Int,
                             override val gameId: String,
                             override val messageId: String
                           ) extends Message(userId: Int, gameId: String, messageId: String)
