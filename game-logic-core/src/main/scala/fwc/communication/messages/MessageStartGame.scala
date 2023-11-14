package fwc.communication.messages

case class MessageStartGame(userId: Int, gameId: String, messageId: String) extends Message
