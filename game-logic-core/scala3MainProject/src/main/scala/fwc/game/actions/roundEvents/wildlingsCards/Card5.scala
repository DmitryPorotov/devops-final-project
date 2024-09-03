package fwc.game.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseWildlingsBids, SubPhaseWildlingsCard, SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack}

case class Card5(gameState: GameState) extends WildlingsCards(gameState) {
  override def resolve(): GameState = {
    if phase.isWin
    then gameStateWithCounterAndBids.copy(
      wildlingCounter = 6,
      subPhase = SubPhaseWildlingsBids(
        HouseType.getSeqOfAll.filter(_ != phase.loserWinnerHouse),
        5,
        gameState.wildlingsStartedFrom12Points.head,
        gameState.wildlingCounter,
      )
    )
    else gameStateWithCounterAndBids.copy(
      subPhase = SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(
        phase.loserWinnerHouse
      )
    )
  }
}
