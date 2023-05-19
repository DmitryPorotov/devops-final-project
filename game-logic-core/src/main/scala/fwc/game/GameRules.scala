package fwc.game

import fwc.game.board.{Board, MilitaryUnitType}
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.planningPhase.{Order, OrderType}
import fwc.gameLoading.HouseCard

case class GameRules(
                    board: Board,
                    kingsCourtStars: Vector[Int],
                    supplyUsage: Vector[Seq[Int]],
                    maxArmies: Map[MilitaryUnitType, Int],
                    boardCards: BoardCards,
                    loadedOrders: Map[OrderType, Seq[Order]],
                    houseCards: Seq[HouseCard]
                    )
