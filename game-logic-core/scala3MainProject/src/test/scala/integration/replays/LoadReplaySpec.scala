package integration.replays

import fwc.game.phases.SubPhase
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.gameSaving.GameReplay
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class LoadReplaySpec extends AnyFlatSpec with should.Matchers {
  "Replay" should "be loaded from JSON" in {
    val jsonStr = fwc.gameLoading.readJson("saves/forIntegration/ReplayAdd2Orders.json")

    val replay = GameReplay.fromJson(jsonStr)

    assert(replay.currentGameState.subPhase.asInstanceOf[SubPhaseAddOrder].houseTypes.size == 6)
  }
}
