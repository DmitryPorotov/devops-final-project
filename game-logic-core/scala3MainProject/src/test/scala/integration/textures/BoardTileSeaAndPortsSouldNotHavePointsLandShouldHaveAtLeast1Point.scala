package integration.textures

import fwc.gameLoading
import fwc.gameLoading.BoardTileType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*


class BoardTileSeaAndPortsSouldNotHavePointsLandShouldHaveAtLeast1Point extends AnyFlatSpec with should.Matchers  {
  "Board tile's neighbours" should "be correct" in {

    val board = gameLoading.loadBoard()
    var homeOfCount = 0
    board.foreach(tile => {
      assert(
        if tile.tileType == BoardTileType.Sea || tile.tileType == BoardTileType.Port
        then
          if (tile.musteringPoints + tile.powerPoints + tile.supplyPoints) > 0
          then throw new RuntimeException(s"Tile ${tile.number} (${tile.tileType}) has a point.")
          else true
        else
          if (tile.musteringPoints + tile.powerPoints + tile.supplyPoints) == 0
          then throw new RuntimeException(s"Tile ${tile.number} (${tile.tileType}) has no points.")
          else
            if tile.homeOf != null && (tile.musteringPoints + tile.powerPoints + tile.supplyPoints) != 4
            then throw new RuntimeException(s"Tile ${tile.number} (${tile.tileType}) home of (${tile.homeOf}) don't have 4 points.")
            else {
              if tile.homeOf != null then homeOfCount += 1
              if !Seq(18, 24, 26, 35).contains(tile.number) && tile.homeOf == null && (tile.musteringPoints + tile.powerPoints + tile.supplyPoints) > 2
              then throw new RuntimeException(s"Tile ${tile.number} (${tile.tileType}) have more then 2 points.")
              else true
            }
      )
    })
    assert(homeOfCount == 6)
  }
}
