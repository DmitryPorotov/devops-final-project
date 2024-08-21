package fwc.game.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.eventsPhase.Bids

case class Card0(gameState: GameState) extends WildlingsCards(gameState) {
  override def resolve(): GameState = {
    gameStateWithCounterAndBidsAndNWPhase.copy(
      wildlingsStartedFrom12Points = None
    )
  }
}
