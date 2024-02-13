package fwc.communication.messages

case class MessageSaveGame(userId: Int, gameId: String, saveName: String, messageId: String) extends Message
