package assetsForTests.gameReplays

import assetsForTests.gameReplays.InitialReplayServerSideRandom.gameReplay
import fwc.communication.reactions.ReactionGameAction
import fwc.game.planningPhase.{Order, OrderType}
import fwc.gameSaving.GameReplay
import fwc.gameSaving.actions.planning.ActionAddOrder

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object ReplayAdd2Orders extends App {
  val jsonStr = fwc.gameLoading.readJson("saves/forIntegration/InitialReplayServerSideRandom.json")

  val replay = GameReplay.fromJson(jsonStr)

  val (replay1, reply1) = ReactionGameAction(
    1,
    replay,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderType.OrderMarch).toJson,
      "houseType" -> "rose",
      "tileNumber" -> 38
    )
  )

  val (replay2, reply2) = ReactionGameAction(
    2,
    replay1,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderType.OrderMarch).toJson,
      "houseType" -> "wolf",
      "tileNumber" -> 3
    )
  )

  Files.write(Paths.get("saves/forIntegration/ReplayAdd2Orders.json"), replay2.toJsonString.getBytes(StandardCharsets.UTF_8))
}
