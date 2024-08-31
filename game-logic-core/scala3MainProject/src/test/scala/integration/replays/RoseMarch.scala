package integration.replays

import fwc.communication.Reactor
import fwc.communication.messagesFromClient.MessageGameAction
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.io.Source.fromFile

class RoseMarch extends AnyFlatSpec with should.Matchers  {
  "Rose march" should "not cause an exception" in {

    val source = fromFile("saves/forIntegration/3--roseMarch--2024-08-31T03-23-38.json")
    val lines = try source.mkString finally source.close

    Reactor.restoreGame(lines)

    val state = Reactor.prepareShutdown("3").currentGameState
    val message = MessageGameAction(-5, "3", ujson.Obj(
      "houseType" -> "rose",
      "sourceTileNumber" -> 41, "targets" -> ujson.Obj("37" -> ujson.Arr(
        ujson.Obj(
          "house"-> "rose",
          "type"-> "ships",
          "isDefeated"-> false,
          "defPoints"-> 0
        )
      )),
      "actionType" -> "resolveMarchOrder"
    ), null)
    Reactor(message, ujson.Obj())
    assert(!Reactor.prepareShutdown("3").currentGameState.subPhase.isInstanceOf[SubPhaseResolveHouseCard])
  }
}
