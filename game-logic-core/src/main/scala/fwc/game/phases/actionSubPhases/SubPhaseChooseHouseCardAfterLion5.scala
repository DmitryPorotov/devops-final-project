package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}
import ujson.Value

case class SubPhaseChooseHouseCardAfterLion5(
                                              houseType: HouseType,
                                              bannedCardCode: Int,
                                              mainPhase: MainPhase = PhaseAction
                                            ) extends SubPhase(mainPhase) with SubPhaseSingleHouse (
  houseType, mainPhase
) {
  def getSubPhaseName: String = "chooseHouseCardAfterLion5"

  override def toJson: Value = 
    val json = super.toJson
    json.obj.addOne("bannedCardCode" -> bannedCardCode)
    json
}