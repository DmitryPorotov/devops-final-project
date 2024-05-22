package fwc.communication.reactions

import fwc.{GameSettings, Player}
import fwc.game.FWCException
import fwc.game.houses.HouseType

import scala.annotation.tailrec
import scala.util.Random

object ReactionStartGame {
  def apply(userId: Int, gameSettings: GameSettings): GameSettings = {
    if gameSettings.ownerId != userId then throw new FWCException("You are not the game owner.")

    if gameSettings.isRandomHouses then
      gameSettings.copy(players = Some(this.setRandomHouses(gameSettings.players.head)))
    else gameSettings
  }

  private def setRandomHouses(players: Seq[Player]): Seq[Player] = {
    val randomHouses = Random.shuffle(HouseType.getSeqOfAll)
    @tailrec
    def addRandomHouse(newPlayers: Seq[Player], players: Seq[Player], houses: Seq[HouseType]): Seq[Player] = {
      if (players.isEmpty)
        newPlayers
      else addRandomHouse(newPlayers :+ players.head.copy(house = Some(houses.head)), players.tail, houses.tail)
    }
    addRandomHouse(Seq(), players, randomHouses)
  }
}
