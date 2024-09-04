package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseSingleHouse}
import ujson.Value

case class SubPhaseChooseHouseCardAfterLion5(
                                              houseType: HouseType,
                                              bannedCardCode: Int,
                                              mainPhase: MainPhase = MainPhase.Action
                                            ) 
  extends SubPhaseSingleHouse (houseType, mainPhase) {
  def getSubPhaseName: String = "chooseHouseCardAfterLion5"

  override def toJson: Value = 
    val json = super.toJson
    json.obj.addOne("bannedCardCode" -> bannedCardCode)
    json
}