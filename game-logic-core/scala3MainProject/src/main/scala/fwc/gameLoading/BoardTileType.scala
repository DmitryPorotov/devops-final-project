package fwc.gameLoading

sealed trait BoardTileType

object BoardTileType {
  case object Sea extends BoardTileType {
    override def toString: String = "sea"
  }

  case object Land extends BoardTileType {
    override def toString: String = "land"
  }

  case object Port extends BoardTileType {
    override def toString: String = "port"
  }


  def fromString(t: String): BoardTileType = t match
    case "sea" => Sea
    case "land" => Land
    case "port" => Port
    case _ => throw new RuntimeException(s"Unknown tile type $t")
}
