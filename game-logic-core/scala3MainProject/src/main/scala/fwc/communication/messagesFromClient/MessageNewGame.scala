package fwc.communication.messagesFromClient

case class MessageNewGame(
                           override val userId: Int,
                           override val gameId: String,
                           override val messageId: String,
                         ) extends Message(userId: Int, gameId: String, messageId: String)