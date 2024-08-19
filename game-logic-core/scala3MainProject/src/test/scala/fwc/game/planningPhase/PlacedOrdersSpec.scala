package fwc.game.planningPhase

import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class PlacedOrdersSpec extends AnyFlatSpec with should.Matchers {

  "PlacedOrders" should "" in {
    val po = PlacedOrders(
      Map(
        HouseType.Wolf -> Map(
          1 -> Order(OrderType.March),
          2 -> Order(OrderType.Raid)
        )
      )
    )

    val m = po.getTileNumToHouseTypeOrderMap
    
    
  }

}
