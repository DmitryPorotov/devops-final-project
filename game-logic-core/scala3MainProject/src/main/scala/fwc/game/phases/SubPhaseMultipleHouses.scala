package fwc.game.phases

import fwc.game.houses.HouseType

trait SubPhaseMultipleHouses(
                              houseTypes: Seq[HouseType],
                              mainPhase: MainPhase = PhasePlanning
                            ) extends SubPhase {
  def toJson: ujson.Value = ujson.Obj(
    "mainPhase" -> mainPhase.toString,
    "subPhase" -> getSubPhaseName,
    "houseTypes" -> ujson.Arr.from(
      houseTypes.map(_.toString)
    )
  )
}
