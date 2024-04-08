package fwc.communication.messages

case class MessageGameAction(
                              override val userId: Int,
                              override val gameId: String,
                              gameAction: ujson.Value,
                              override val messageId: String,
                            ) extends Message(userId: Int, gameId: String, messageId: String)
