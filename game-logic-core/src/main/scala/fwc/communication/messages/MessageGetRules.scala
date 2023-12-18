package fwc.communication.messages

case class MessageGetRules(userId: Int, gameId: String, messageId: String) extends Message
