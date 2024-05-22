package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.Mustering
import fwc.game.houses.HouseWolf
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class MusterKnightsSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "be able to muster knights" in {
    val gameState = fwc.game.initializeGameState()

    val newGameState = Mustering.musterGroundUnit(
      3,
      MilitaryUnit(HouseWolf, MilitaryUnitKnights),
      gameState,
    )

    assert(
      newGameState._2.points(gameRules.board(3)) == 2,
      "2 mustering points should be used at tile 3")

    assert(
      newGameState._1.getOrElse(3, null).count(_.unitType == MilitaryUnitKnights) == 2,
      "there should be 2 knights at tile 3"
    )
  }
}
