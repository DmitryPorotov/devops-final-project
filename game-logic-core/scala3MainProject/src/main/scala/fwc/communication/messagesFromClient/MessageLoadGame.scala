package fwc.communication.messagesFromClient

case class MessageLoadGame(
                            override val userId: Int,
                            override val gameId: String, 
                            saveName: String,
                            override val messageId: String
                          ) extends Message(userId: Int, gameId: String, messageId: String)
