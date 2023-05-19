package fwc.communication.messages

case class MessageJoinGame(
                          userId: Int,
                          gameId: String,
                          joinAs: AnyRef
                          )
  extends Message
