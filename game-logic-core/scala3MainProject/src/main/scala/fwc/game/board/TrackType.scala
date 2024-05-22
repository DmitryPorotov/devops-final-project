package fwc.game.board

import fwc.game.FWCException

sealed trait TrackType

case object TrackThrone extends TrackType {
  override def toString: String = "throne"
}

case object TrackFiefdoms extends TrackType {
  override def toString: String = "fiefdoms"
}

case object TrackCourt extends TrackType {
  override def toString: String = "court"
}

object TrackType {
  def fromString(str: String): TrackType = str match
    case "throne" => TrackThrone
    case "fiefdoms" => TrackFiefdoms
    case "court" => TrackCourt
    case t => throw new FWCException(s"Unknown track $t")
}
