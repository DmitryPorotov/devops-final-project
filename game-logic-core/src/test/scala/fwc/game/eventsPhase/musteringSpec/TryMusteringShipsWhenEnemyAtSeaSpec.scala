package fwc.game.eventsPhase.musteringSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.eventsPhase.*
import fwc.game.houses.{HouseKraken, HouseWolf}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class TryMusteringShipsWhenEnemyAtSeaSpec extends AnyFlatSpec with should.Matchers {

  "Mustering object musterShips function" should "throw an exception when trying to muster to an enemy occupied sea" in {
    val gameState = fwc.game.initializeGameState()
    val gameState1 = gameState.copy(
      armies = gameState.armies +
        (0 -> Seq[MilitaryUnit](
          MilitaryUnit(HouseKraken, MilitaryUnitShips)
        ))
    )


    try{
      Mustering.musterShips(
        3,
        0,
        MilitaryUnit(HouseWolf, MilitaryUnitShips),
        gameState1,
      )
    } catch {
      case e: MusteringException => assert(e.getMessage == "Can't muster ships if there are enemy ships in the sea", "Should throw \"Can't muster ships if there are enemy ships in the sea\" exception")
      case e: Throwable => Failed(e)
    }
  }
}
