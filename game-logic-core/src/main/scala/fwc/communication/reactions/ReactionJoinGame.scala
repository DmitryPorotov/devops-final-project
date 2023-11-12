package fwc.communication.reactions

import fwc.{GameSettings, Player}
import fwc.communication.messages.MessageJoinGame
import fwc.game.houses.HouseType

object ReactionJoinGame {
  def apply(userId: Int, houseType: Option[HouseType], gameSettings: GameSettings): GameSettings =
    gameSettings.copy(players = Some((gameSettings.players getOrElse Seq[Player]()) appended Player(userId, houseType)))
}
