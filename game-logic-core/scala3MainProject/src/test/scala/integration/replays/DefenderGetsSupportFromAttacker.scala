package integration.replays

import fwc.communication.Reactor
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveSupportOrder
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class DefenderGetsSupportFromAttacker extends AnyFlatSpec with should.Matchers {
  "The defender" should "not be able to request and get support from the attacker" in {
    val source = fromFile("saves/forIntegration/3--defenderGetsSupportFromAttacker--2024-10-07T12-40-00.json")
    val lines = try source.mkString finally source.close


    Reactor.restoreGameDebug(lines, Some((actNum: Int, oldState: GameState, act: Action, newState: GameState) => {
      if actNum >= 10
      then
        val a = 0
    }))

    val game = Reactor.prepareShutdown("3").currentGameState
    assert(!game.combat.defenderSupport.contains(37))
    assert(game.subPhase.isInstanceOf[SubPhaseResolveSupportOrder])
    val phase = game.subPhase.asInstanceOf[SubPhaseResolveSupportOrder]
    assert(phase.houseType == HouseType.Rose)
    assert(phase.tilesNumbers.contains(37))
    assert(phase.tilesNumbers.contains(39))
  }
}
