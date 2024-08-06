package fwc.communication.messagesFromClient

case class MessageTryJoinGame(
                               override val userId: Int,
                               override val gameId: String,
                               override val messageId: String,
                             ) 
  extends Message(userId: Int, gameId: String, messageId: String)
