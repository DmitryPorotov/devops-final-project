package fwc.communication.messages

case class MessageListSaves(
                             userId: Int,
                             gameId: String,
                             messageId: String,
                           )
  extends Message
