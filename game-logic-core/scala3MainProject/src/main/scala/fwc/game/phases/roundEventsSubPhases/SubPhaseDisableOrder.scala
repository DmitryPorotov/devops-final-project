package fwc.game.phases.roundEventsSubPhases

import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseNoHouse}
import fwc.game.planningPhase.OrderType
import ujson.Value

case class SubPhaseDisableOrder(
                                orderType: OrderType,
                                mainPhase: MainPhase = PhaseRoundEvents
                              )extends SubPhase(mainPhase) with SubPhaseNoHouse(
  mainPhase
) {
  def getSubPhaseName: String = "disableOrder"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("orderType" -> orderType.toString)
    json
}
