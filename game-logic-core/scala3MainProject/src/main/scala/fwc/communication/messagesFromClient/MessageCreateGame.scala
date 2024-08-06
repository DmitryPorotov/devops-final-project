package fwc.communication.messagesFromClient

case class MessageCreateGame(
                            override val userId: Int,
                            override val gameId: String, 
                            isRandomHouses: Boolean,
                            isInputOnly: Boolean,
                            override val messageId: String
                            ) extends Message(userId: Int, gameId: String, messageId: String)
