package fwc.game.phases

trait SubPhaseNoHouse(
                       mainPhase: MainPhase
                     ) extends SubPhase {
  override def toJson: ujson.Value = ujson.Obj(
    "mainPhase" -> mainPhase.toString,
    "subPhase" -> getSubPhaseName
  )
}
