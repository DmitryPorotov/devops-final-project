package fwc.communication.messages

case class MessageTryJoinGame(
                             userId: Int,
                             gameId: String
                             ) 
  extends Message
