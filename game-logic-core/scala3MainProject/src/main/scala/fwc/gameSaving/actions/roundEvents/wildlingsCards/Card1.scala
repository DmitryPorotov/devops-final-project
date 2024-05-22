package fwc.gameSaving.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.board.TrackThrone
import fwc.game.eventsPhase.{Bids, Supplies}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseDisbandUnit
import fwc.gameSaving.actions.roundEvents.*

case class Card1(gameState: GameState) extends WildlingsCards(gameState) {

  override def resolve(): GameState = {
    val updatedSupplies =
      if phase.isWin
      then
        val sum = gameState.supplies(phase.loserWinnerHouse) + 1
        gameState.supplies +
          (phase.loserWinnerHouse -> (if sum > 6 then 6 else sum))
      else
        val allHousesSupplies = phase.houseTypes.foldLeft(Map[HouseType, Int]())(
          (acc, cur) =>
            acc + {
              val sum =
                (if cur == phase.loserWinnerHouse then -2 else -1)
                  + gameState.supplies(cur)
              cur -> (if sum < 0 then 0 else sum)
            }
        )
        gameState.supplies ++ allHousesSupplies

    val toConsolidate = Supplies.findArmiesToConsolidate(gameState.armies, gameState.supplies).filter(_._2.nonEmpty)

    val newPhase =
      if toConsolidate.isEmpty
      then getNextNonWildlingsPhase
      else SubPhaseDisbandUnit(
        Supplies.getHouseToConsolidate(toConsolidate, gameState.tracks(TrackThrone)),
        if gameState.wildlingsStartedFrom12Points.head then UnitDisbandNextStepDeck1 else UnitDisbandNextStepPlanningPhase
      )

    gameStateWithCounterAndBids.copy(
      subPhase = newPhase,
      supplies = updatedSupplies,
      wildlingsStartedFrom12Points = None
    )
  }
  
}
