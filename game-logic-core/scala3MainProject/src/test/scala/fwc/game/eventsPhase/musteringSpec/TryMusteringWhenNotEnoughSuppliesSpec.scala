package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.{Mustering, MusteringException}
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringWhenNotEnoughSuppliesSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "throw an exception when trying to muster units when there is not enough supplies" in {
    val gameState = fwc.game.initializeGameState()

    val gameState1 = gameState.copy(
      armies = gameState.armies + (1 -> Seq[MilitaryUnit](
          MilitaryUnit(HouseType.Wolf, MilitaryUnitType.SiegeEngines),
          MilitaryUnit(HouseType.Wolf, MilitaryUnitType.SiegeEngines),
          MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Footmen),
        ))
    )

    try {
      Mustering.musterGroundUnit(
        7,
        MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Footmen),
        gameState1,
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "House wolf does not have enough supplies to muster footmen", "Should throw \"House wolf does not have enough supplies to muster footmen\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
