package fwc.game.eventsPhase

import fwc.game.board.*
import fwc.game.houses.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class SuppliesSpec extends AnyFlatSpec with should.Matchers {

  "eventsPhase object" should "recalculate supplies track" in {
    val gameState = fwc.game.initializeGameState()

    val newSupplyTrack = Supplies.recalculateSupplyTrack(gameState.armies)

    assert(newSupplyTrack.supplies.getOrElse(HouseKraken, -1) == 1)
    assert(newSupplyTrack.supplies.getOrElse(HouseWolf, -1) == 1)
    assert(newSupplyTrack.supplies.getOrElse(HouseLion, -1) == 2)
    assert(newSupplyTrack.supplies.getOrElse(HouseRose, -1) == 2)
    assert(newSupplyTrack.supplies.getOrElse(HousePufferfish, -1) == 2)
    assert(newSupplyTrack.supplies.getOrElse(HouseMoose, -1) == 2)
  }

  "eventsPhase object findArmiesToConsolidate function" should "find armies to consolidate" in {
    val gameState = fwc.game.initializeGameState()

    val testArmies =
      gameState.armies + (27 -> (
        gameState.armies(27)
          :+ MilitaryUnit(HouseLion, MilitaryUnitFootmen)
          :+ MilitaryUnit(HouseLion, MilitaryUnitFootmen)
//          :+ MilitaryUnit(HouseLion, Footmen)
        ))
        + (21 -> (gameState.armies(21) :+ MilitaryUnit(HouseLion, MilitaryUnitShips)))
        + (22 -> Seq(MilitaryUnit(HouseLion, MilitaryUnitFootmen),MilitaryUnit(HouseLion, MilitaryUnitFootmen),MilitaryUnit(HouseLion, MilitaryUnitKnights)))
        + (15 -> Seq(MilitaryUnit(HouseKraken, MilitaryUnitShips),MilitaryUnit(HouseKraken, MilitaryUnitShips),MilitaryUnit(HouseKraken, MilitaryUnitShips)))
        + (17 -> Seq(MilitaryUnit(HouseKraken, MilitaryUnitShips),MilitaryUnit(HouseKraken, MilitaryUnitShips),MilitaryUnit(HouseKraken, MilitaryUnitShips)))
        + (7 -> Seq(MilitaryUnit(HouseWolf, MilitaryUnitFootmen),MilitaryUnit(HouseWolf, MilitaryUnitFootmen)))
        + (2 -> Seq(MilitaryUnit(HouseWolf, MilitaryUnitShips),MilitaryUnit(HouseWolf, MilitaryUnitShips)))


    val toConsolidate = Supplies.findArmiesToConsolidate(testArmies,
      Supplies(gameState.supplies.supplies + (HouseLion -> 0))
    )

    assert(toConsolidate(HouseLion).size == 2)
    assert(toConsolidate(HouseKraken).size == 2)
    assert(toConsolidate(HouseWolf).size == 3)
    assert(toConsolidate(HouseRose).isEmpty)
    assert(toConsolidate(HouseMoose).isEmpty)
    assert(toConsolidate(HousePufferfish).isEmpty)

    val toConsolidate1 = Supplies.findArmiesToConsolidate(
      testArmies,
      Supplies(gameState.supplies.supplies + (HouseLion -> 0)),
      HouseKraken
    )

    assert(toConsolidate1(HouseKraken).size == 2)
  }

}
