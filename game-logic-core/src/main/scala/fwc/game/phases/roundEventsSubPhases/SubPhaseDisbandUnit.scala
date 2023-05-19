package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseSingleHouse}
import fwc.gameSaving.actions.roundEvents.UnitDisbandNextStepType
import ujson.Value


case class SubPhaseDisbandUnit(
                                houseType: HouseType,
                                nextStep: UnitDisbandNextStepType,
                                mainPhase: MainPhase = PhaseRoundEvents
                              ) extends SubPhaseSingleHouse(
    houseType, mainPhase
  ) {
  def getSubPhaseName: String = "disbandUnit"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("nextStep" -> nextStep.toString)
    json
}
