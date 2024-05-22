package fwc.game.planningPhase

import fwc.game.houses.{HouseType, HouseWolf}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class PlacedOrdersSpec extends AnyFlatSpec with should.Matchers {

  "PlacedOrders" should "" in {
    val po = PlacedOrders(
      Map(
        HouseWolf -> Map(
          1 -> Order(OrderMarch),
          2 -> Order(OrderRaid)
        )
      )
    )

    val m = po.getTileNumToHouseTypeOrderMap
    
    
  }

}
