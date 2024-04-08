package fwc.communication.messages

case class MessageTestConnectivity (
                                     override val userId: Int,
                                     override val messageId: String
                                   ) extends Message(userId: Int, "-1", messageId: String)