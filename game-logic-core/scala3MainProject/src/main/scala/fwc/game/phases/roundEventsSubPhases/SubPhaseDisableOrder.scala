package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import fwc.game.planningPhase.OrderType
import ujson.Value

case class SubPhaseDisableOrder(
                                houseTypes: Seq[HouseType],
                                orderType: OrderType,
                                mainPhase: MainPhase = MainPhase.RoundEvents
                              )
  extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
    with SubPhasePassive  {
  def getSubPhaseName: String = "disableOrder"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("orderType" -> orderType.toString)
    json
}
