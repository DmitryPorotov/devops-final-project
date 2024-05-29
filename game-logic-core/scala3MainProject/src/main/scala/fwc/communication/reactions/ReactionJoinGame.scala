package fwc.communication.reactions

import fwc.{GameSettings, Player}
import fwc.game.FWCException
import fwc.game.houses.HouseType

object ReactionJoinGame {
  def apply(userId: Int, houseType: Option[HouseType], name: String, gameSettings: GameSettings): GameSettings =
    if !gameSettings.isRandomHouses then
      if houseType.isEmpty then throw new FWCException("Must choose a house")

    if houseType.isDefined
      && gameSettings.players.isDefined
      && gameSettings.players.head.nonEmpty
      then {
      if gameSettings.players.head.foldLeft(false)(
        (acc, player) =>
          if player.house == houseType.head then
            true
          else acc
      )
      then
        throw new FWCException("Other player has selected this house already")
      val player = gameSettings.players.head.find(_.userId == userId)
      if player.nonEmpty && player.head.house.nonEmpty && player.head.house.head == houseType.head then
        return gameSettings
      else if player.nonEmpty then
        throw new FWCException(s"You already joined as ${player.head.house.head}")
    }
    val updatedSettings = gameSettings.copy(players = Some((gameSettings.players getOrElse Seq[Player]()) appended Player(userId, name, houseType)))
    updatedSettings
}
