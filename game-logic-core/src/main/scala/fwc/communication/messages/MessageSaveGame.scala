package fwc.communication.messages

case class MessageSaveGame(
                            override val userId: Int,
                            override val gameId: String, 
                            saveName: String,
                            override val messageId: String
                          ) extends Message(userId: Int, gameId: String, messageId: String)
