package fwc.communication.messages

case class MessageCreateGame(
                            override val userId: Int,
                            override val gameId: String, 
                            isRandomHouses: Boolean,
                            override val messageId: String
                            ) extends Message(userId: Int, gameId: String, messageId: String)
