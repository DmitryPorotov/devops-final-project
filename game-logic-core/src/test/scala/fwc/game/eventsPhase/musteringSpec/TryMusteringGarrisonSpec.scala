package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.{Mustering, MusteringException}
import fwc.game.houses.HouseWolf
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringGarrisonSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "throw an exception when trying to muster a garrison" in {
    val gameState = fwc.game.initializeGameState()

    try {
      Mustering.musterGroundUnit(
        3,
        MilitaryUnit(HouseWolf, MilitaryUnitGarrison),
        gameState,
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "Can't muster a garrison", "Should throw \"Can't muster a garrison\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
