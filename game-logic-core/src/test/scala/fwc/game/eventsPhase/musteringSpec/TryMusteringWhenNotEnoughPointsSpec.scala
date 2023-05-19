package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.{Mustering, MusteringException}
import fwc.game.houses.HouseWolf
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringWhenNotEnoughPointsSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterGroundUnit function" should "throw an exception when trying to muster on a tile which used all its mustering points" in {
    val gameState = fwc.game.initializeGameState()

    val newGameState = Mustering.musterGroundUnit(
      3,
      MilitaryUnit(HouseWolf, MilitaryUnitKnights),
      gameState,
    )

    try {
      Mustering.musterGroundUnit(
        3,
        MilitaryUnit(HouseWolf, MilitaryUnitKnights),
        gameState.copy(
          armies = newGameState._1,
          usedMusteringPoints = newGameState._2
        ),
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "Not enough points to muster knights", "Should throw \"Not enough points to muster knights\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
