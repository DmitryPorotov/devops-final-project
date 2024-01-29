package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}

case class SubPhaseResolveHouseCard(
                                    houseType: HouseType,
                                    cardCode: Int,
                                    mainPhase: MainPhase = PhaseAction
                                  ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
  houseType, mainPhase
) {
  def getSubPhaseName: String = "resolveHouseCard"

  override def toJson: ujson.Value = {
    val json = super.toJson
    json.obj.addOne("cardCode" -> cardCode)
    json
  }
}
