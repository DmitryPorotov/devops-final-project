package fwc.game.board

import fwc.game.FWCException

sealed trait TrackType



object TrackType {

  case object Throne extends TrackType {
    override def toString: String = "throne"
  }

  case object Fiefdoms extends TrackType {
    override def toString: String = "fiefdoms"
  }

  case object Court extends TrackType {
    override def toString: String = "court"
  }

  def fromString(str: String): TrackType = str match
    case "throne" => Throne
    case "fiefdoms" => Fiefdoms
    case "court" => Court
    case t => throw new FWCException(s"Unknown track $t")
}
