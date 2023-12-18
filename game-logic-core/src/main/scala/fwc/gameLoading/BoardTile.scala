package fwc.gameLoading

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import ujson.Value

case class BoardTile(number: Int,
                     tileType: BoardTileType,
                     name: String,
                     neighbourTiles: Seq[Int],
                     musteringPoints: Int = 0,
                     supplyPoints: Int = 0,
                     powerPoints: Int = 0,
                     homeOf: HouseType = null)
  extends JsonSerializable {
  def isNeighbourOf(boardTile: BoardTile): Boolean = {
    neighbourTiles.contains(boardTile.number)
  }

  def isNeighbourOf(boardTileNumber: Int): Boolean = {
    neighbourTiles.contains(boardTileNumber)
  }
  def toNumberString: String = this.number.toString

  override def toJson: Value = ujson.Obj(
    "tileType" -> tileType.toString,
    "name" -> name,
    "neighbourTiles" -> neighbourTiles,
    "musteringPoints" -> musteringPoints,
    "supplyPoints" -> supplyPoints,
    "powerPoints" -> powerPoints,
    "homeOf" -> (if homeOf != null then homeOf.toString else ujson.Null)
  )
}
