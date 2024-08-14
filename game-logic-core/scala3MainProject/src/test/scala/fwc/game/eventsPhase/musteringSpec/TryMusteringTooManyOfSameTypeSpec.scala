package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.{Mustering, MusteringException}
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringTooManyOfSameTypeSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "throw an exception when trying to muster more units of the same type then allowed" in {
    val gameState = fwc.game.initializeGameState()

    val gameState1 = gameState.copy(
      armies = gameState.armies + (1 -> Seq[MilitaryUnit](
        MilitaryUnit(HouseType.Wolf, MilitaryUnitType.SiegeEngines),
        MilitaryUnit(HouseType.Wolf, MilitaryUnitType.SiegeEngines),
      ))
    )

    try {
      Mustering.musterGroundUnit(
        3,
        MilitaryUnit(HouseType.Wolf, MilitaryUnitType.SiegeEngines),
        gameState1,
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "Maximum count of siegeEngines reached", "Should throw \"Maximum count of siegeEngines reached\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
