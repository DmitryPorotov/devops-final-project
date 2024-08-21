package fwc.game.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseWildlingsKillUnits, SubPhaseWildlingsMusterAtCastle}

case class Card6(gameState: GameState) extends WildlingsCards(gameState) {
  override def resolve(): GameState = {
    if phase.isWin
    then gameStateWithCounterAndBids.copy(
      SubPhaseWildlingsMusterAtCastle(phase.loserWinnerHouse)
    )
    else gameStateWithCounterAndBids.copy(
      subPhase = SubPhaseWildlingsKillUnits(
        phase.houseTypes.foldLeft(Map[HouseType, Int]())(
          (acc, cur) =>
            acc + (cur -> (if cur == phase.loserWinnerHouse then 2 else 1))
        ),
        Some(phase.loserWinnerHouse)
      )
    )
  }
}
