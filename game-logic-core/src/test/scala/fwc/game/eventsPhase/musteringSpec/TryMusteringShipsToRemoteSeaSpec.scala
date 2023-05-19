package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.*
import fwc.game.houses.HouseWolf
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringShipsToRemoteSeaSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterShips function" should "throw an exception when trying to unconnected sea" in {
    val gameState = fwc.game.initializeGameState()

    try{
      Mustering.musterShips(
        3,
        14,
        MilitaryUnit(HouseWolf, MilitaryUnitShips),
        gameState,
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "Winterfell is not a neighbor of The Narrow Sea", "Should throw \"Winterfell is not a neighbor of The Narrow Sea\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
