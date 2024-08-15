package fwc.game.planningPhase

import fwc.game.board.TileNumber
import fwc.{JsonParsable, JsonSerializable}
import fwc.game.{FWCException, gameRules}
import fwc.game.houses.HouseType
import fwc.gameLoading.{BoardTileLand, BoardTilePort, BoardTileType}
import ujson.Value

import scala.util.Try


case class PlacedOrders(placedOrders: Map[HouseType, Map[TileNumber, Order]] = Map()) extends JsonSerializable {

  export placedOrders.{apply, get, getOrElse, flatMap}

  def toJson: ujson.Value = {
    ujson.Obj(
      upickle.core.LinkedHashMap(
        placedOrders.map((houseType, orders: Map[TileNumber, Order]) => {
          houseType.toString -> ujson.Obj(
            upickle.core.LinkedHashMap(
              orders.map((tileNum: TileNumber, order: Order) =>
                tileNum.toString -> order.toJson
              )
            )
          )
        })
      )
    )
  }

  def getTileNumToHouseTypeOrderMap: Map[Int, (HouseType, Order)] = {
    placedOrders.flatMap((houseType, tileOrder: Map[TileNumber, Order]) => {
      tileOrder.map((t, o) => t -> (houseType, o))
    })
  }

  def getOrderByTileNumber(tileNumber: TileNumber): Option[(HouseType, Order)] = {
    getTileNumToHouseTypeOrderMap.get(tileNumber)
  }

  def placeOrder(houseType: HouseType, tileNumber: TileNumber, order: Order, positionOnCourtTrack: Int): PlacedOrders = {
    if order.isStar && !hasEnoughStars(houseType, positionOnCourtTrack)
    then throw new FWCException("Cannot use a special order")
    if placedOrders.getOrElse(houseType, Map()).contains(tileNumber)
    then throw new FWCException(s"There is an order on this tile \"${gameRules.board(tileNumber).name}\" already")
    copy(
      placedOrders + (
        houseType -> (placedOrders.getOrElse(houseType, Map()) + (
          tileNumber -> order
          )
        )
      )
    )
  }

  def removeOrder(houseType: HouseType, tileNumber: TileNumber): PlacedOrders = {
    copy(placedOrders + (houseType
      -> (placedOrders.getOrElse(houseType, Map()) - tileNumber)
      )
    )
  }

  def getSupportOrdersForTile(sourceTileNumber: TileNumber): Map[TileNumber, (HouseType, Order)] = {
    val sourceTile = gameRules.board(sourceTileNumber)
    val neighboursTileNumbers = sourceTile.neighbourTiles
    val isRelevantTile = if sourceTile.tileType == BoardTileLand
      then (t: BoardTileType) => t != BoardTilePort
      else (t: BoardTileType) => t != BoardTileLand
    placedOrders.flatMap((houseType: HouseType, orders: Map[TileNumber, Order]) =>
      orders.foldLeft[Map[TileNumber, (HouseType, Order)]](Map())(
        (acc: Map[TileNumber, (HouseType, Order)], tnOrder: (TileNumber, Order)) =>
          if neighboursTileNumbers.contains(tnOrder._1)
            && tnOrder._2.orderType == OrderType.OrderSupport
            && isRelevantTile(gameRules.board(tnOrder._1).tileType)
          then acc + (tnOrder._1 -> (houseType, tnOrder._2))
          else acc
      )
    )
  }

  private def hasEnoughStars(houseType: HouseType, positionOnCourtTrack: Int): Boolean = {
    val availStars = Try[Int](gameRules.kingsCourtStars(positionOnCourtTrack)).getOrElse(0)
    val usedStars =
      if placedOrders.contains(houseType)
      then placedOrders(houseType).count(_._2.isStar)
      else 0
    (availStars - usedStars) > 0
  }
}

object PlacedOrders extends JsonParsable {
  override def fromJson(json: Value): PlacedOrders = {
    PlacedOrders(
      json.obj.map((houseType, orders: ujson.Value) =>
        HouseType.fromString(houseType) -> orders.obj.map(
          (tileNum: String, order: ujson.Value) 
            => tileNum.toInt -> Order.fromJson(order)
        ).toMap
      ).toMap
    )
  }
}