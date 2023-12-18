package fwc.game

import fwc.JsonSerializable
import fwc.game.board.{Board, MilitaryUnitType}
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.planningPhase.{Order, OrderType}
import fwc.gameLoading.HouseCard
import ujson.Value

case class GameRules(
                    board: Board,
                    kingsCourtStars: Vector[Int],
                    supplyUsage: Vector[Seq[Int]],
                    maxArmies: Map[MilitaryUnitType, Int],
                    boardCards: BoardCards,
                    loadedOrders: Map[OrderType, Seq[Order]],
                    houseCards: Seq[HouseCard]
                    ) 
  extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "board" -> board.map(_.toJson),
    "kingsCourtStars" -> kingsCourtStars,
    "supplyUsage" -> supplyUsage,
    "maxArmies" -> maxArmies.map((mut, n) => mut.toString -> n),
    "boardCards" -> boardCards.toJson,
    "loadedOrders" -> loadedOrders.map((ot, ods) => ot.toString -> ods.map(_.toJson)),
    "houseCards" -> houseCards.map(_.toJson)
  )
}
