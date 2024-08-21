package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseSingleHouse}

case class SubPhaseResolveSupportOrder(
                                        override val houseType: HouseType,
                                        tilesNumbers: Seq[Int],
                                        override val mainPhase: MainPhase = PhaseAction
                                      ) extends SubPhase(mainPhase) with SubPhaseSingleHouse (
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
