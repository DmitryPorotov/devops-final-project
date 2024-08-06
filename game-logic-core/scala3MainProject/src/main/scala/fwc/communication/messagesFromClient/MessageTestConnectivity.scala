package fwc.communication.messagesFromClient

case class MessageTestConnectivity (
                                     override val userId: Int,
                                     override val messageId: String
                                   ) extends Message(userId: Int, "-1", messageId: String)