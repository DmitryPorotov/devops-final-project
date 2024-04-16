package fwc

import fwc.game.actionPhase.{DiscardedHouseCards, DominanceTokensUsage}
import fwc.game.board.*
import fwc.game.eventsPhase.{Bids, PowerTokens, Supplies, UsedMusteringPoints}
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.houses.*
import fwc.game.phases.SubPhaseAwaitingStart
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.planningPhase.{AvailableOrders, PlacedOrders}
import fwc.gameLoading.BoardStart

import scala.util.Random

package object game {
  def initializeGameState(randomEventsServerSide: Boolean = true): GameState = {
    val boardStart = gameLoading.loadBoardStart()

    val initArmies = board.initializeArmies(boardStart)

    val tracks = Tracks.initialize(boardStart)

    val otherRules = gameLoading.loadOtherRules()

    //TODO should be SubPhaseAwaitingStart
    val initPhase = SubPhaseAddOrder(HouseType.getSeqOfAll) //SubPhaseAwaitingStart()

    GameState(
      initPhase,
      initArmies,
      PlacedOrders(),
      tracks,
      Supplies.initialize(boardStart),
      DiscardedHouseCards(),
      PowerTokens.initialize(otherRules("numOfTokens").asInstanceOf[Int]),
      if randomEventsServerSide then BoardCards.initialize() else BoardCards.initializeEmpty(),
      DominanceTokensUsage(),
      UsedMusteringPoints(),
      AvailableOrders.initialize(),
      Bids()
    )
  }


  val gameRules: GameRules = {
    val board = gameLoading.loadBoard()
    val otherRules = gameLoading.loadOtherRules()
    val courtStars = otherRules("kingsCourtStars").asInstanceOf[Vector[Int]]
    val supplyUsage = otherRules("supplyUsage").asInstanceOf[Vector[Seq[Int]]]
    val maxArmies = otherRules("maxArmies").asInstanceOf[Map[MilitaryUnitType, Int]]
    val boardCards = BoardCards.initializeForRules(
      gameLoading.loadRoundEventCards(),
      gameLoading.loadWildlingCards(),
      gameLoading.loadTideOfBattleCards()
    )
    val orders = gameLoading.loadAvailableOrders()
    val houseCards = gameLoading.loadHouseCards()
    
    GameRules(
      board,
      courtStars,
      supplyUsage,
      maxArmies,
      boardCards,
      orders,
      houseCards
    )
  }

}
