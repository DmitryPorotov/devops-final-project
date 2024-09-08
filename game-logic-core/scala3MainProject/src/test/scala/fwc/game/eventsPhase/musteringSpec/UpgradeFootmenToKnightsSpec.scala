package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.Mustering
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class UpgradeFootmenToKnightsSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "be able to upgrade footmen to knights" in {
    val gameState = fwc.game.initializeGameState()

    val newGameState = Mustering.musterGroundUnit(
      3,
      MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Knights),
      gameState,
      true,
    )

    assert(
      newGameState._2(gameRules.board(3)) == 1,
      "1 mustering points should be used at tile 3, after upgrading footmen to knights")

    assert(
      newGameState._1.getOrElse(3, null).count(_.unitType == MilitaryUnitType.Knights) == 2,
      "there should be 2 knights at tile 3"
    )

    assert(
      newGameState._1.getOrElse(3, null).count(_.unitType == MilitaryUnitType.Footmen) == 0,
      "there should be 0 footmen at tile 3"
    )

    val newGameState2 = Mustering.musterGroundUnit(
      3,
      MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Footmen),
      gameState.copy(
        armies = newGameState._1,
        usedMusteringPoints = newGameState._2
      ),
    )

    assert(
      newGameState2._2(gameRules.board(3)) == 2,
      "2 mustering points should be used at tile 3, after upgrading and mustering footmen")
  }
}
