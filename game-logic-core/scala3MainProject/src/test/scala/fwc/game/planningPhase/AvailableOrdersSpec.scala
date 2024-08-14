package fwc.game.planningPhase

import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import fwc.game.planningPhase.*
import fwc.gameLoading

class AvailableOrdersSpec extends AnyFlatSpec with should.Matchers {

  "AvailableOrders.scala" should "load initialize available orders" in {
    val ao = AvailableOrders.initialize()

    assert(ao.orders.isInstanceOf[Map[HouseType, Map[OrderType, Seq[Order]]]])
  }

  "AvailableOrders.scala" should "be able to use an order" in {
    val ao = AvailableOrders.initialize()

    val ao2 = ao.useOrder(HouseType.Wolf, Order(OrderDefend, modifier = 1))

    assert(ao2.orders(HouseType.Wolf)(OrderDefend).length == 2, "there should be 2 orders left after use")

    val ao3 = ao2.useOrder(HouseType.Wolf, Order(OrderRaid))

    assert(ao3.orders(HouseType.Wolf)(OrderRaid).length == 2, "there should be 2 orders left after use")
  }

}
