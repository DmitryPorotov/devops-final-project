package integration.textures

import fwc.gameLoading
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*


class BoardTileNeighboursShouldBeCorrect extends AnyFlatSpec with should.Matchers  {
  "Board tile's neighbours" should "be correct" in {

    val board = gameLoading.loadBoard()

    board.foreach(tile => {
      tile.neighbourTiles.foreach(nt => {
        if !board(nt).neighbourTiles.contains(tile.number)
        then throw new RuntimeException(s"Tile ${nt} neighbours did not contain $tile")
      })
    })
  }
}
