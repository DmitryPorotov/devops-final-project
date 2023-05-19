package fwc.game

import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class PackageSpec extends AnyFlatSpec with should.Matchers {

  "Game package" should "initialize the game state" in {
    val gameState = fwc.game.initializeGameState()

    assert(gameState.isInstanceOf[GameState])
  }

  "Game package" should "initialize the game rules" in {
    val gameRules = fwc.game.gameRules

    assert(gameRules.isInstanceOf[GameRules])
  }

}