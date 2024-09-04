package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import fwc.game.planningPhase.OrderType
import ujson.Value

case class SubPhaseDisableOrder(
                                houseTypes: Seq[HouseType],
                                orderType: OrderType,
                              override val mainPhase: MainPhase = MainPhase.RoundEvents
                              )extends SubPhase(mainPhase) 
  with SubPhasePassive(mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase) 
  {
  def getSubPhaseName: String = "disableOrder"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("orderType" -> orderType.toString)
    json
}
