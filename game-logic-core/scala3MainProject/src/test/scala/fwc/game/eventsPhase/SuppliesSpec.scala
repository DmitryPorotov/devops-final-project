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

    assert(newSupplyTrack.supplies.getOrElse(HouseType.Kraken, -1) == 1)
    assert(newSupplyTrack.supplies.getOrElse(HouseType.Wolf, -1) == 1)
    assert(newSupplyTrack.supplies.getOrElse(HouseType.Lion, -1) == 2)
    assert(newSupplyTrack.supplies.getOrElse(HouseType.Rose, -1) == 2)
    assert(newSupplyTrack.supplies.getOrElse(HouseType.PufferFish, -1) == 2)
    assert(newSupplyTrack.supplies.getOrElse(HouseType.Moose, -1) == 2)
  }

  "eventsPhase object findArmiesToConsolidate function" should "find armies to consolidate" in {
    val gameState = fwc.game.initializeGameState()

    val testArmies =
      gameState.armies + (27 -> (
        gameState.armies(27)
          :+ MilitaryUnit(HouseType.Lion, MilitaryUnitType.Footmen)
          :+ MilitaryUnit(HouseType.Lion, MilitaryUnitType.Footmen)
//          :+ MilitaryUnit(HouseLion, Footmen)
        ))
        + (21 -> (gameState.armies(21) :+ MilitaryUnit(HouseType.Lion, MilitaryUnitType.Ships)))
        + (22 -> Seq(MilitaryUnit(HouseType.Lion, MilitaryUnitType.Footmen),MilitaryUnit(HouseType.Lion, MilitaryUnitType.Footmen),MilitaryUnit(HouseType.Lion, MilitaryUnitType.Knights)))
        + (15 -> Seq(MilitaryUnit(HouseType.Kraken, MilitaryUnitType.Ships),MilitaryUnit(HouseType.Kraken, MilitaryUnitType.Ships),MilitaryUnit(HouseType.Kraken, MilitaryUnitType.Ships)))
        + (17 -> Seq(MilitaryUnit(HouseType.Kraken, MilitaryUnitType.Ships),MilitaryUnit(HouseType.Kraken, MilitaryUnitType.Ships),MilitaryUnit(HouseType.Kraken, MilitaryUnitType.Ships)))
        + (7 -> Seq(MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Footmen),MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Footmen)))
        + (2 -> Seq(MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Ships),MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Ships)))


    val toConsolidate = Supplies.findArmiesToConsolidate(testArmies,
      Supplies(gameState.supplies.supplies + (HouseType.Lion -> 0))
    )

    assert(toConsolidate(HouseType.Lion).size == 2)
    assert(toConsolidate(HouseType.Kraken).size == 2)
    assert(toConsolidate(HouseType.Wolf).size == 3)
    assert(toConsolidate(HouseType.Rose).isEmpty)
    assert(toConsolidate(HouseType.Moose).isEmpty)
    assert(toConsolidate(HouseType.PufferFish).isEmpty)

    val toConsolidate1 = Supplies.findArmiesToConsolidate(
      testArmies,
      Supplies(gameState.supplies.supplies + (HouseType.Lion -> 0)),
      HouseType.Kraken
    )

    assert(toConsolidate1(HouseType.Kraken).size == 2)
  }

}
