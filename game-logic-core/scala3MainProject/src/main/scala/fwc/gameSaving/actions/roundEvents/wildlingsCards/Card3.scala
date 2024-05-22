package fwc.gameSaving.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseWildlingsKillUnits, SubPhaseWildlingsReturnHouseCard}

case class Card3(gameState: GameState) extends WildlingsCards(gameState) {
  override def resolve(): GameState = {
    if phase.isWin
    then gameStateWithCounterAndBids.copy(
      subPhase = SubPhaseWildlingsReturnHouseCard(phase.loserWinnerHouse)
    )
    else gameStateWithCounterAndBids.copy(
      subPhase = SubPhaseWildlingsKillUnits(
        phase.houseTypes.foldLeft(Map[HouseType, Int]())(
          (acc, cur) =>
            acc + (cur -> (if cur == phase.loserWinnerHouse then 3 else 2))
        )
      )
    )
  }
}
