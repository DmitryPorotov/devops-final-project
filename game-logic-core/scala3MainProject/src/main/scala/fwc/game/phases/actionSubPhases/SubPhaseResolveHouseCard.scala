package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseSingleHouse}

case class SubPhaseResolveHouseCard(
                                     override val houseType: HouseType,
                                      cardCode: Int,
                                     override val mainPhase: MainPhase = MainPhase.Action
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
