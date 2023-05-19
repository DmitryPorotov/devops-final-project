package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.Mustering
import fwc.game.houses.HouseWolf
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class MusterShipsSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterShips function" should "be able to muster ships at sea and in a port" in {
    val gameState = fwc.game.initializeGameState()

    val newGameState = Mustering.musterShips(
      3,
      0,
      MilitaryUnit(HouseWolf, MilitaryUnitShips),
      gameState,
    )

    assert(
      newGameState._2.points(gameRules.board(3)) == 1,
      "1 mustering points should be used at tile 3")

    assert(
      newGameState._1.getOrElse(0, null).count(_.unitType == MilitaryUnitShips) == 1,
      "there should be 1 ships at tile 0"
    )

    val newGameState2 = Mustering.musterShips(
      3,
      4,
      MilitaryUnit(HouseWolf, MilitaryUnitShips),
      gameState.copy(
        armies = newGameState._1,
        usedMusteringPoints = newGameState._2
      ),
    )

    assert(
      newGameState2._2.points(gameRules.board(3)) == 2,
      "2 mustering points should be used at tile 3")

    assert(
      newGameState2._1.getOrElse(4, null).count(_.unitType == MilitaryUnitShips) == 1,
      "there should be 1 ships at tile 4"
    )
  }
}
