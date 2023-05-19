package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.{Mustering, MusteringException}
import fwc.game.houses.HouseWolf
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringAtTheEnemyTileSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "throw an exception when trying to muster at enemy tile" in {
    val gameState = fwc.game.initializeGameState()

    try {
      Mustering.musterGroundUnit(
        16,
        MilitaryUnit(HouseWolf, MilitaryUnitFootmen),
        gameState,
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "This tile does not belong to wolf", "Should throw \"This tile does not belong to wolf\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
