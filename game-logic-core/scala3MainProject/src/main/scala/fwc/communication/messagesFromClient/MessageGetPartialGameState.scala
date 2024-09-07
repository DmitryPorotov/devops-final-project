package fwc.communication.messagesFromClient

case class MessageGetPartialGameState(
                                    override val userId: Int,
                                    override val gameId: String,
                                    parts: Seq[String],
                                    override val messageId: String
                                  ) extends Message(userId: Int, gameId: String, messageId: String)
