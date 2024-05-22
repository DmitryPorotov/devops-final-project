package fwc.communication.messages

case class MessageListSaves(
                             override val userId: Int,
                             override val gameId: String,
                             override val messageId: String,
                           )
  extends Message(userId: Int, gameId: String, messageId: String)
