package fwc.game.phases

trait SubPhasePassive(
                     override val mainPhase: MainPhase
                     ) extends SubPhase {
  override def toJson: ujson.Value = ujson.Obj(
    "mainPhase" -> mainPhase.toString,
    "subPhase" -> getSubPhaseName
  )
}
