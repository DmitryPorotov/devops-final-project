package fwc.communication.messages

import fwc.game.houses.HouseType

case class MessageAddBots(
                          override val userId: Int,
                          override val gameId: String,
                          houseTypes: List[HouseType],
                          override val messageId: String
                        ) extends Message(userId: Int, gameId: String, messageId: String)
