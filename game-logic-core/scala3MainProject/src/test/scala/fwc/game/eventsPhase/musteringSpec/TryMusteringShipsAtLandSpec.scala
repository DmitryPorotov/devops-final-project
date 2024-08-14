package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.*
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringShipsAtLandSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterShips function" should "throw an exception when trying to muster on land" in {
    val gameState = fwc.game.initializeGameState()

    try{
      Mustering.musterShips(
        3,
        3,
        MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Ships),
        gameState,
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "A ship must be mustered on a sea or in a port", "Should throw \"A ship must be mustered on a sea or in a port\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
