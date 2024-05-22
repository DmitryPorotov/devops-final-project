package fwc.communication.messages

import fwc.game.houses.HouseType

case class MessageJoinGame(
                            override val userId: Int,
                            override val gameId: String,
                            joinAs: Option[HouseType],
                            name: String,
                            override val messageId: String,
                          )
  extends Message(userId: Int, gameId: String, messageId: String)
