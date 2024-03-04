package fwc.communication.messages

case class MessageLoadGame(userId: Int, gameId: String, saveName: String, messageId: String) extends Message
