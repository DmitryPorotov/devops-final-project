package fwc.communication.reactions

import fwc.{GameSettings, Player}
import fwc.game.GameState

import java.util.UUID

object ReactionCreateGame {
  def apply(userId: Int, gameId: String): (String, GameSettings, GameState) = {
    val finalGameId = if gameId.isBlank then UUID.randomUUID().toString else gameId
    val randomEventsServerSide = true
    val settings = GameSettings(
      finalGameId,
      userId,
      false,
      false,
      randomEventsServerSide,
      Some(Seq(Player(userId, null))),
      None
    )
    val state = fwc.game.initializeGameState(randomEventsServerSide)
    (finalGameId, settings, state)
  }
}
