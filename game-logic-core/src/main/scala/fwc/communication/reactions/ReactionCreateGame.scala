package fwc.communication.reactions

import fwc.{GameSettings, Player}
import fwc.game.GameState

import java.util.UUID

object ReactionCreateGame {
  def apply(userId: Int): (String, GameSettings, GameState) = {
    val gameId = UUID.randomUUID().toString
    val randomEventsServerSide = true
    val settings = GameSettings(
      gameId,
      userId,
      false,
      false,
      randomEventsServerSide,
      Some(Seq(Player(userId, null))),
      None
    )
    val state = fwc.game.initializeGameState(randomEventsServerSide)
    (gameId, settings, state)
  }
}
