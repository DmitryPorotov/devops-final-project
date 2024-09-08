package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.{Mustering, UsedMusteringPoints}
import fwc.game.houses.HouseType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class MusterFootmenSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "be able to muster footmen" in {
    val gameState = fwc.game.initializeGameState()

    val (armies, usedMusteringPoints) = Mustering.musterGroundUnit(
      7,
      MilitaryUnit(HouseType.Wolf, MilitaryUnitType.Footmen),
      gameState,
    )

    assert(
      usedMusteringPoints(gameRules.board(7)) == 1,
      "1 mustering point should be used at tile 7"
    )

    assert(
      armies.getOrElse(7, null).count(_.unitType == MilitaryUnitType.Footmen) == 2,
      "there should be 2 footmen at tile 7"
    )
  }
}
