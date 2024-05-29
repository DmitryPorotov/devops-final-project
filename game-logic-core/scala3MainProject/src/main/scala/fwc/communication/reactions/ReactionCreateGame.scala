package fwc.communication.reactions

import fwc.GameSettings
import fwc.game.GameState

import java.util.UUID

object ReactionCreateGame {
  def apply(userId: Int, gameId: String, isRandomHouses: Boolean): (String, GameSettings, GameState) = {
    val randomEventsServerSide = true
    val settings = GameSettings(
      gameId,
      UUID.randomUUID(),
      userId,
      false,
      isRandomHouses,
      randomEventsServerSide,
      None,
      None
    )
    val state = fwc.game.initializeGameState(randomEventsServerSide)
    (gameId, settings, state)
  }
}
