package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.Mustering
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class MusterAtEmptyHomeTile extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "be able to muster footmen on en empty home tile" in {
    val gameState = fwc.game.initializeGameState()

    val gameState1 = gameState.copy(armies = gameState.armies - 3)

    val newGameState = Mustering.musterGroundUnit(
      3,
      MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Footmen),
      gameState1,
    )

    assert(
      newGameState._2(gameRules.board(3)) == 1,
      "1 mustering point should be used at tile 7"
    )

    assert(
      newGameState._1.getOrElse(3, null).count(_.unitType == MilitaryUnitType.Footmen) == 1,
      "there should be 1 footmen at tile 3"
    )
  }
}
