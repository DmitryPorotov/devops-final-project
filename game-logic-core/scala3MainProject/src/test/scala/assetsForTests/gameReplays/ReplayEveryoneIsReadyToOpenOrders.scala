package assetsForTests.gameReplays

import assetsForTests.gameReplays.ReplayAddAllOrders.replay14
import fwc.communication.reactions.ReactionGameAction
import fwc.game.planningPhase.Order
import fwc.gameSaving.GameReplay

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object ReplayEveryoneIsReadyToOpenOrders extends App {
  val jsonStr = fwc.gameLoading.readJson("saves/forIntegration/ReplayAllOrders.json")

  val replay = GameReplay.fromJson(jsonStr)

  val (replay1, reply1) = ReactionGameAction(
    1,
    replay,
    ujson.Obj(
      "actionType" -> "openOrders",
      "houseType" -> "rose",
    )
  )

  val (replay2, reply2) = ReactionGameAction(
    2,
    replay1,
    ujson.Obj(
      "actionType" -> "openOrders",
      "houseType" -> "wolf",
    )
  )

  val (replay3, reply3) = ReactionGameAction(
    3,
    replay2,
    ujson.Obj(
      "actionType" -> "openOrders",
      "houseType" -> "lion",
    )
  )

  val (replay4, reply4) = ReactionGameAction(
    4,
    replay3,
    ujson.Obj(
      "actionType" -> "openOrders",
      "houseType" -> "moose",
    )
  )

  val (replay5, reply5) = ReactionGameAction(
    5,
    replay4,
    ujson.Obj(
      "actionType" -> "openOrders",
      "houseType" -> "pufferfish",
    )
  )

  val (replay6, reply6) = ReactionGameAction(
    6,
    replay5,
    ujson.Obj(
      "actionType" -> "openOrders",
      "houseType" -> "kraken",
    )
  )

  Files.write(Paths.get("saves/forIntegration/ReplayEveryoneIsReadyToOpenOrders.json"), replay6.toJsonString.getBytes(StandardCharsets.UTF_8))
}
