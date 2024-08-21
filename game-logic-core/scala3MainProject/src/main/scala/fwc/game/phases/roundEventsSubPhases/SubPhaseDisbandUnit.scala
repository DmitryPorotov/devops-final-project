package fwc.game.phases.roundEventsSubPhases

import fwc.game.actions.roundEvents.UnitDisbandNextStepType
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseSingleHouse}
import ujson.Value


case class SubPhaseDisbandUnit(
                                override val houseType: HouseType,
                                nextStep: UnitDisbandNextStepType,
                                override val mainPhase: MainPhase = PhaseRoundEvents
                              ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
    houseType, mainPhase
  ) {
  def getSubPhaseName: String = "disbandUnit"

  override def toJson: Value =
    val json = super.toJson
    json.obj.addOne("nextStep" -> nextStep.toString)
    json
}
