package fwc.communication.messages

case class MessageGetGameState(userId: Int, gameId: String, messageId: String) extends Message
