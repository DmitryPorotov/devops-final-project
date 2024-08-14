package fwc.game.board

import fwc.game.houses.HouseType
import fwc.gameLoading
import fwc.game.board
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class BoardSpec extends AnyFlatSpec with should.Matchers {

  "Board package" should "initialize the starting armies" in {
    val boardStart = gameLoading.loadBoardStart()

    val initArmies = board.initializeArmies(boardStart)

    assert(initArmies.isInstanceOf[Armies])
  }

}
