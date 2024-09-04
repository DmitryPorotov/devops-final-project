package fwc.game.phases

import fwc.game.houses.HouseType
import fwc.game.phases.MainPhase.Planning

abstract class SubPhaseMultipleHouses(
                                      houseTypes: Seq[HouseType],
                                      mainPhase: MainPhase = Planning
                                      )
  extends SubPhase(mainPhase) {
  override def toJson: ujson.Value =
    ujson.Obj(
    "mainPhase" -> mainPhase.toString,
    "subPhase" -> getSubPhaseName,
    "houseTypes" -> ujson.Arr.from(
      houseTypes.map(_.toString)
    )
  )
}
