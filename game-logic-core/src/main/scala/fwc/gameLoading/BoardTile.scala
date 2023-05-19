package fwc.gameLoading

import fwc.game.houses.HouseType

case class BoardTile(number: Int,
                     tileType: BoardTileType,
                     name: String,
                     neighbourTiles: Seq[Int],
                     musteringPoints: Int = 0,
                     supplyPoints: Int = 0,
                     powerPoints: Int = 0,
                     homeOf: HouseType = null) {
  def isNeighbourOf(boardTile: BoardTile): Boolean = {
    neighbourTiles.contains(boardTile.number)
  }

  def isNeighbourOf(boardTileNumber: Int): Boolean = {
    neighbourTiles.contains(boardTileNumber)
  }
  def toNumberString: String = this.number.toString
}
