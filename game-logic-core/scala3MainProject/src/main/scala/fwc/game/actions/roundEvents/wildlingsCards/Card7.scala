package fwc.game.actions.roundEvents.wildlingsCards

import fwc.game.{GameState, gameRules}
import fwc.game.board.MilitaryUnitType
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.roundEventsSubPhases.{SubPhaseWildlingsDowngradeKnights, SubPhaseWildlingsUpgradeKnights}

case class Card7(gameState: GameState) extends WildlingsCards(gameState) {
  override def resolve(): GameState = {
    if phase.isWin
    then
      val numFootmen = gameState.armies.countUnitsByTypeAndHouse(MilitaryUnitType.Footmen, phase.loserWinnerHouse)
      val numKnights = gameState.armies.countUnitsByTypeAndHouse(MilitaryUnitType.Knights, phase.loserWinnerHouse)

      if numFootmen == 0 || numKnights >= gameRules.maxArmies(MilitaryUnitType.Knights)
      then gameStateWithCounterAndBids.copy(getNextNonWildlingsPhase, wildlingsStartedFrom12Points = None)
      else gameStateWithCounterAndBids.copy(SubPhaseWildlingsUpgradeKnights(phase.loserWinnerHouse))
    else
      val houseTypes =
        phase.houseTypes.foldLeft(Map[HouseType, Int]())(
          (acc, cur: HouseType) =>
            val tuple = {
              val allKnights = gameState.armies.countUnitsByTypeAndHouse(MilitaryUnitType.Knights, cur)
              val numToDowngrade =
                if cur == phase.loserWinnerHouse
                then allKnights
                else if allKnights > 2
                then 2
                else allKnights
              cur -> numToDowngrade
            }

            if tuple._2 == 0
            then acc
            else acc + tuple
        )

      if houseTypes.isEmpty
      then gameStateWithCounterAndBids.copy(getNextNonWildlingsPhase, wildlingsStartedFrom12Points = None)
      else gameStateWithCounterAndBids.copy(SubPhaseWildlingsDowngradeKnights(houseTypes))
  }
}
