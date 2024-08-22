package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseSingleHouse}
import ujson.Value

case class SubPhaseChooseHouseCardAfterLion5(
                                              override val houseType: HouseType,
                                              bannedCardCode: Int,
                                              override val mainPhase: MainPhase = MainPhase.Action
                                            ) extends SubPhase(mainPhase) with SubPhaseSingleHouse (
  houseType, mainPhase
) {
  def getSubPhaseName: String = "chooseHouseCardAfterLion5"

  override def toJson: Value = 
    val json = super.toJson
    json.obj.addOne("bannedCardCode" -> bannedCardCode)
    json
}