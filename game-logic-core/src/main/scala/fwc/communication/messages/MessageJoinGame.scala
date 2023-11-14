package fwc.communication.messages

import fwc.game.houses.HouseType

case class MessageJoinGame(
                          userId: Int,
                          gameId: String,
                          joinAs: Option[HouseType],
                          messageId: String
                          )
  extends Message
