package integration.replays

import fwc.game.phases.SubPhase
import fwc.game.phases.planningSubPhases.SubPhaseReadyToOpenOrders
import fwc.gameSaving.GameReplay
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class ReplayAllOrdersRound1Spec extends AnyFlatSpec with should.Matchers {
  "Replay" should "be loaded from JSON" in {
    val jsonStr = fwc.gameLoading.readJson("saves/forIntegration/ReplayAllOrders.json")

    val replay = GameReplay.fromJson(jsonStr)

    assert(replay.currentGameState.subPhase.asInstanceOf[SubPhaseReadyToOpenOrders].houseTypes.size == 6)
    assert(replay.currentGameState.roundCounter == 1)
  }
}
