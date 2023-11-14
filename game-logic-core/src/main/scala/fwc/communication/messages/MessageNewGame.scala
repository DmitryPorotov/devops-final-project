package fwc.communication.messages

case class MessageNewGame(
                           gameId: String,
                           messageId: String,
                         ) extends Message