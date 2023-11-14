package fwc.communication.messages

case class MessageCreateGame( userId: Int, gameId: String, isRandomHouses: Boolean, messageId: String) extends Message
