package fwc.communication.messages

case class MessageGameAction(
                              userId: Int,
                              gameId: String,
                              gameAction: ujson.Value
                            ) extends Message
