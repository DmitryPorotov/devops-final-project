package fwc.game.planningPhase

import fwc.game.houses.*
import enrichment.ExtSeq
import fwc.game.board.{TrackType, Tracks}
import fwc.{JsonParsable, JsonSerializable}
import fwc.game.{FWCException, gameRules}
import ujson.Value

import scala.util.boundary
import scala.util.Try

case class AvailableOrders(
                            orders: Map[HouseType, Map[OrderType, Seq[Order]]]
                          ) extends JsonSerializable {

  def isOrderAvailable(house: HouseType, order: Order): Boolean = {
    this.orders(house)(order.orderType)
      .exists(o => o.isStar == order.isStar && o.modifier == order.modifier)
  }

  def useOrder(house: HouseType, order: Order): AvailableOrders = {
    if (!isOrderAvailable(house, order)) {
      throw new FWCException(s"Order \"${order.orderType} ${if order.modifier != 0 then order.modifier else ""}\" is not available")
    }

    val newOrders = orders(house)(order.orderType).deleteFirstMatch(order)

    AvailableOrders(
      orders + (
        house -> (
          orders(house) + (order.orderType -> newOrders)
        )
      )
    )

  }

  def returnOrder(house: HouseType, order: Order): AvailableOrders = {
    AvailableOrders(
      orders + (
        house -> (
          orders(house) + (
            order.orderType -> (
              orders(house)(order.orderType) :+ order
            )
          )
        )
      )
    )
  }

  def disableOrderType(orderType: OrderType): AvailableOrders = {
    if (orderType == OrderType.March)
      throw new FWCException("cannot disable all march orders")
      
    AvailableOrders(
      this.orders.map((ht, orders) => {
        ht -> (orders + (orderType -> Seq()))
      })
    )
  }
  
  def disableMarchPlusOneOrder(): AvailableOrders = {
    AvailableOrders(
      this.orders.map((ht: HouseType, orders: Map[OrderType, Seq[Order]]) => {
        ht -> (
          orders
            + (OrderType.March -> orders(OrderType.March).filter(!_.isStar))
          ) 
      })
    )
  }

  def toJson: ujson.Value = {
    ujson.Obj(
      upickle.core.LinkedHashMap(
        orders.map((houseType, ordersByType: Map[OrderType, Seq[Order]]) => {
          houseType.toString -> ujson.Obj(
            upickle.core.LinkedHashMap(
              ordersByType.map((orderType, ordersSeq: Seq[Order]) => {
                orderType.toString -> ujson.Value(
                  ordersSeq.map(_.toJson)
                )
              })
            )
          )
        })
      )
    )
  }

  def hasAvailableOrders(houseType: HouseType, tracks: Tracks): Boolean = boundary {
    val availableStars = Try(gameRules.kingsCourtStars(tracks(TrackType.Court).indexOf(houseType))).getOrElse(0)
    val starsShouldBeLeft = 5 - availableStars
    
    val ordersLeft = orders(houseType).view.flatten[Order](_._2).foldLeft(0)(
      (acc, cur) =>
        if cur.isStar then acc + 1 else boundary.break(false)
    )
    ordersLeft > starsShouldBeLeft
  }

}

object AvailableOrders extends JsonParsable {
  def initialize(): AvailableOrders = {
    AvailableOrders(
      Map(
        HouseType.Wolf -> gameRules.loadedOrders,
        HouseType.Moose -> gameRules.loadedOrders,
        HouseType.PufferFish -> gameRules.loadedOrders,
        HouseType.Kraken -> gameRules.loadedOrders,
        HouseType.Rose -> gameRules.loadedOrders,
        HouseType.Lion -> gameRules.loadedOrders
      )
    )
  }

  override def fromJson(json: Value): AvailableOrders = {
    AvailableOrders(
      json.obj.map((houseType, ordersByType: Value) => 
        HouseType.fromString(houseType) -> ordersByType.obj.map((orderType, orders: Value) => 
          OrderType.fromString(orderType) -> orders.arr.map(o => Order.fromJson(o)).toSeq
        ).toMap
      ).toMap
    )
  }
}
