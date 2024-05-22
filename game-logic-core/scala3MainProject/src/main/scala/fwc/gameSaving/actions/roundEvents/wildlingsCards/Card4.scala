package fwc.gameSaving.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.eventsPhase.Bids

case class Card4(gameState: GameState) extends WildlingsCards(gameState) {
  override def resolve(): GameState = {
    if phase.isWin
    then return gameStateWithCounterAndBidsAndNWPhase.copy(
      powerTokens = gameState.powerTokens + (phase.loserWinnerHouse -> (
        gameState.powerTokens(phase.loserWinnerHouse)
          + gameState.bids(phase.loserWinnerHouse)
        ))
    )

    val updatedPowerTokens = phase.houseTypes.foldLeft(gameState.powerTokens)(
      (acc, cur) =>
        acc + (cur -> {
          val sum = if cur == phase.loserWinnerHouse then 0 else acc(cur) - 2
          if sum < 0 then 0 else sum
        }
          )
    )
    gameStateWithCounterAndBidsAndNWPhase.copy(
      powerTokens = updatedPowerTokens,
    )
  }

}
