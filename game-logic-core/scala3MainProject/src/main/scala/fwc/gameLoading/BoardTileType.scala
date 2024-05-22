package fwc.gameLoading

sealed trait BoardTileType

case object BoardTileSea extends BoardTileType {
  override def toString: String = "sea"
}

case object BoardTileLand extends BoardTileType {
  override def toString: String = "land"
}

case object BoardTilePort extends BoardTileType {
  override def toString: String = "port"
}

object BoardTileType {
  def fromString(t: String): BoardTileType = t match
    case "sea" => BoardTileSea
    case "land" => BoardTileLand
    case "port" => BoardTilePort
    case _ => throw new RuntimeException(s"Unknown tile type $t")
}
