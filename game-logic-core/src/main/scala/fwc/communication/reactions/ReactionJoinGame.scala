package fwc.communication.reactions

import fwc.{GameSettings, Player}
import fwc.communication.messages.MessageJoinGame
import fwc.game.FWCException
import fwc.game.houses.HouseType

object ReactionJoinGame {
  def apply(userId: Int, houseType: Option[HouseType], name: String, gameSettings: GameSettings): GameSettings =
    if !gameSettings.isRandomHouses then
      if houseType.isEmpty then throw new FWCException("Must choose a house")

    if houseType.isDefined
      && gameSettings.players.isDefined
      && gameSettings.players.head.nonEmpty
      && gameSettings.players.head.foldLeft(false)(
      (acc, player) =>
        if player.house == houseType.head then
          true
        else false
    )
    then
      throw new FWCException("Other player has selected this house already")
    gameSettings.copy(players = Some((gameSettings.players getOrElse Seq[Player]()) appended Player(userId, name, houseType)))
}
