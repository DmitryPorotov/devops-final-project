package fwc.gameSaving.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.board.Tracks
import fwc.game.eventsPhase.Bids
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsCard
import fwc.gameSaving.actions.roundEvents.EventCards

trait WildlingsCards(gameState: GameState){
  protected val phase: SubPhaseWildlingsCard = gameState.subPhase.asInstanceOf[SubPhaseWildlingsCard]

  protected val updatedWildlingsCounter: Int =
    if phase.isWin
    then 0
    else
      val sum = gameState.wildlingCounter - 4
      if sum < 0 then 0 else sum

  protected def getNextNonWildlingsPhase: SubPhase =
    WildlingsCards.getNextNonWildlingsPhase(gameState.wildlingsStartedFrom12Points.head, gameState.tracks, gameState.boardCards)

  protected val gameStateWithCounterAndBids: GameState =
    gameState.copy(
      wildlingCounter = updatedWildlingsCounter,
      bids = Bids()
    )

  protected val gameStateWithCounterAndBidsAndNWPhase: GameState =
    gameStateWithCounterAndBids.copy(getNextNonWildlingsPhase)

  def resolve(): GameState
}

object WildlingsCards {
  def resolveCard(gameState: GameState): GameState = {
    gameState.subPhase.asInstanceOf[SubPhaseWildlingsCard].cardCode match
      case 0 => Card0(gameState).resolve()
      case 1 => Card1(gameState).resolve()
      case 2 => Card2(gameState).resolve()
      case 3 => Card3(gameState).resolve()
      case 4 => Card4(gameState).resolve()
      case 5 => Card5(gameState).resolve()
      case 6 => Card6(gameState).resolve()
      case 7 => Card7(gameState).resolve()
      case 8 => Card8(gameState).resolve()
  }

  def getNextNonWildlingsPhase(isWildlingsCounter12: Boolean, tracks: Tracks, boardCards: BoardCards): SubPhase =
    if isWildlingsCounter12
    then EventCards.fallThroughFromDeck1(tracks, boardCards)
    else SubPhaseAddOrder(HouseType.getSeqOfAll)
}
