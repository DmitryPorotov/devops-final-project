package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}

case class SubPhaseResolveSupportOrder(
                                        houseType: HouseType,
                                        tilesNumbers: Seq[Int],
                                        mainPhase: MainPhase = PhaseAction
                                      ) extends SubPhaseSingleHouse (
  houseType, mainPhase
  ) {
  def getSubPhaseName: String = "resolveSupportOrder"

  override def toJson: ujson.Value = {
    val json = super.toJson
    json.obj.addOne(
      "tilesNumbers" -> ujson.Arr.from(
        tilesNumbers
      )
    )
    json
  }
}
