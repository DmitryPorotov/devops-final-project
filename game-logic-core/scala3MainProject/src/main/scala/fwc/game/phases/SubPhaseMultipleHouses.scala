package fwc.game.phases

import fwc.game.houses.HouseType
import fwc.game.phases.MainPhase.Planning

trait SubPhaseMultipleHouses(
                              houseTypes: Seq[HouseType],
                              mainPhase: MainPhase = Planning
                            ) extends SubPhase {
  override def toJson: ujson.Value = ujson.Obj(
    "mainPhase" -> mainPhase.toString,
    "subPhase" -> getSubPhaseName,
    "houseTypes" -> ujson.Arr.from(
      houseTypes.map(_.toString)
    )
  )
}
