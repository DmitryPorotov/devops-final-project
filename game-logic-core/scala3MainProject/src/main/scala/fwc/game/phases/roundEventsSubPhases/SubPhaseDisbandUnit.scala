package fwc.game.phases.roundEventsSubPhases

import fwc.game.actions.roundEvents.UnitDisbandNextStepType
import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value


case class SubPhaseDisbandUnit(
                                houseType: HouseType,
                                nextStep: UnitDisbandNextStepType,
                                mainPhase: MainPhase = MainPhase.RoundEvents
                              ) 
  extends SubPhaseSingleHouse(houseType, mainPhase) {
  def getSubPhaseName: String = "disbandUnit"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("nextStep" -> nextStep.toString)
    json
}
