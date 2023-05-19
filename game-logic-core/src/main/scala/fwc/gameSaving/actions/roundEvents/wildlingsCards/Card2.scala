package fwc.gameSaving.actions.roundEvents.wildlingsCards

import fwc.game.actionPhase.DiscardedHouseCards
import fwc.game.eventsPhase.Bids
import fwc.game.houses.HouseType
import fwc.game.{GameState, gameRules}
import fwc.game.phases.roundEventsSubPhases.{SubPhaseWildlingsCard, SubPhaseWildlingsDiscardHouseCard}
import fwc.gameLoading.HouseCard

case class Card2(gameState: GameState) extends WildlingsCards(gameState) {

  override def resolve(): GameState = {
    if phase.isWin
    then return gameStateWithCounterAndBidsAndNWPhase.copy(
      discardedHouseCards = gameState.discardedHouseCards - phase.loserWinnerHouse,
      wildlingsStartedFrom12Points = None
    )

    val loserDisCards: Seq[Int] = gameState.discardedHouseCards(phase.loserWinnerHouse)

    val updatedLoserDisCards =
      if loserDisCards.size == 6
      then Some(loserDisCards)
      else
        val loserCards = getLoserCards(phase.loserWinnerHouse, loserDisCards)
        val maxStrength = getMaxStrength(loserCards)
        val loserCardsMaxStr = loserCards.filter(_.strength == maxStrength)

        if loserCardsMaxStr.size == loserCards.size
        then None
        else Some(loserDisCards ++ loserCardsMaxStr.map(_.code))

    val housesWhoNeedToDiscardACard =
      getHousesWhoNeedToDiscardACard(phase.houseTypes, phase.loserWinnerHouse, gameState.discardedHouseCards)
        ++ (if updatedLoserDisCards.isEmpty then Seq(phase.loserWinnerHouse) else Seq())


    gameStateWithCounterAndBids.copy(
      subPhase =
        if housesWhoNeedToDiscardACard.nonEmpty
        then SubPhaseWildlingsDiscardHouseCard(housesWhoNeedToDiscardACard)
        else getNextNonWildlingsPhase,
      discardedHouseCards =
        if updatedLoserDisCards.isEmpty
        then gameState.discardedHouseCards
        else gameState.discardedHouseCards + (phase.loserWinnerHouse -> updatedLoserDisCards.head),
      wildlingsStartedFrom12Points =
        if housesWhoNeedToDiscardACard.nonEmpty
        then gameStateWithCounterAndBids.wildlingsStartedFrom12Points
        else None
    )
  }

  def getLoserCards(loserHouse: HouseType, loserDiscardedCards: Seq[Int]): Seq[HouseCard] = {
    gameRules.houseCards.filter(
      hc => hc.house == loserHouse
        && !loserDiscardedCards.contains(hc.code)
    )
  }

  def getMaxStrength(cards: Seq[HouseCard]): Int = {
    cards.foldLeft(0)(
      (acc, cur) => if cur.strength > acc then cur.strength else acc
    )
  }

  def getHousesWhoNeedToDiscardACard(
                                      allHouses: Seq[HouseType],
                                      loserHouse: HouseType,
                                      discardedHouseCards: DiscardedHouseCards
                                    ): Seq[HouseType] = {
    allHouses.filter(h =>
      h != loserHouse
        && discardedHouseCards(h).size < 6
    )
  }
}
