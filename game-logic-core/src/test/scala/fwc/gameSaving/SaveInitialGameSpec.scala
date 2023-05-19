package fwc.gameSaving

import org.scalatest.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import fwc.gameSaving

class SaveInitialGameSpec extends AnyFlatSpec with should.Matchers{
  "Game saving package" should "be able to save initial game state" in {
    val gameState = fwc.game.initializeGameState()

//    gameSaving.saveGame("initGameState", gameState.toJson)
  }
}
