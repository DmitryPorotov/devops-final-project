package integration.replays

import fwc.communication.Reactor
import fwc.game.GameState
import fwc.game.board.MilitaryUnitType
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class PowerTokenIsErasedOnRetreatBug extends AnyFlatSpec with should.Matchers  {
  "Power token of the same army" should "not be erased when the army retreats" in {

    val source = fromFile("saves/forIntegration/3--powerTokenIsErasedOnRetreatBug--2024-09-15T09-33-13.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGameDebug(lines, Some((actNum, currentState, action, newState: GameState) =>{
      if newState.armies.exists(_._1 == 40) && newState.armies(40).exists(x => x.unitType == MilitaryUnitType.PowerToken)
      then
        val a = 0
    }))

    val gameState = Reactor.prepareShutdown("3").currentGameState
    assert(gameState.armies(40).size == 2)
    assert(gameState.armies(40).exists(_.unitType == MilitaryUnitType.PowerToken))

  }
}
