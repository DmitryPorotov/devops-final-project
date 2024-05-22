package assetsForTests.gameReplays

import fwc.communication.reactions.ReactionGameAction
import fwc.game.planningPhase.{Order, OrderConsolidatePower, OrderMarch, OrderSupport}
import fwc.gameSaving.GameReplay

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object ReplayAddAllOrders extends App {

  val jsonStr = fwc.gameLoading.readJson("saves/forIntegration/InitialReplayServerSideRandom.json")

  val initReplay = GameReplay.fromJson(jsonStr)

  val (replay, reply) = ReactionGameAction(
    1,
    initReplay,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch).toJson,
      "houseType" -> "rose",
      "tileNumber" -> 38
    )
  )

  val (replay2, reply2) = ReactionGameAction(
    2,
    replay,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch).toJson,
      "houseType" -> "wolf",
      "tileNumber" -> 3
    )
  )

  val (replay3, reply3) = ReactionGameAction(
    2,
    replay2,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch, modifier = -1).toJson,
      "houseType" -> "wolf",
      "tileNumber" -> 2
    )
  )

  val (replay4, reply4) = ReactionGameAction(
    2,
    replay3,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderConsolidatePower).toJson,
      "houseType" -> "wolf",
      "tileNumber" -> 7
    )
  )

  val (replay5, reply5) = ReactionGameAction(
    4,
    replay4,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch).toJson,
      "houseType" -> "moose",
      "tileNumber" -> 31
    )
  )

  val (replay6, reply6) = ReactionGameAction(
    4,
    replay5,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch, modifier = -1).toJson,
      "houseType" -> "moose",
      "tileNumber" -> 30
    )
  )

  val (replay7, reply7) = ReactionGameAction(
    4,
    replay6,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderConsolidatePower).toJson,
      "houseType" -> "moose",
      "tileNumber" -> 40
    )
  )

  val (replay8, reply8) = ReactionGameAction(
    1,
    replay7,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch, modifier = -1).toJson,
      "houseType" -> "rose",
      "tileNumber" -> 44
    )
  )

  val (replay9, reply9) = ReactionGameAction(
    1,
    replay8,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderSupport).toJson,
      "houseType" -> "rose",
      "tileNumber" -> 41
    )
  )

  val (replay10, reply10) = ReactionGameAction(
    3,
    replay9,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch).toJson,
      "houseType" -> "lion",
      "tileNumber" -> 22
    )
  )

  val (replay11, reply11) = ReactionGameAction(
    3,
    replay10,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderSupport).toJson,
      "houseType" -> "lion",
      "tileNumber" -> 21
    )
  )

  val (replay12, reply12) = ReactionGameAction(
    3,
    replay11,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch, modifier = -1).toJson,
      "houseType" -> "lion",
      "tileNumber" -> 27
    )
  )


  val (replay13, reply13) = ReactionGameAction(
    5,
    replay12,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch).toJson,
      "houseType" -> "pufferfish",
      "tileNumber" -> 55
    )
  )

  val (replay14, reply14) = ReactionGameAction(
    5,
    replay13,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderSupport).toJson,
      "houseType" -> "pufferfish",
      "tileNumber" -> 50
    )
  )

  val (replay15, reply15) = ReactionGameAction(
    5,
    replay14,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch, modifier = -1).toJson,
      "houseType" -> "pufferfish",
      "tileNumber" -> 54
    )
  )

  val (replay16, reply16) = ReactionGameAction(
    6,
    replay15,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderConsolidatePower).toJson,
      "houseType" -> "kraken",
      "tileNumber" -> 17
    )
  )

  val (replay17, reply17) = ReactionGameAction(
    6,
    replay16,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderSupport).toJson,
      "houseType" -> "kraken",
      "tileNumber" -> 15
    )
  )

  val (replay18, reply18) = ReactionGameAction(
    6,
    replay17,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch).toJson,
      "houseType" -> "kraken",
      "tileNumber" -> 16
    )
  )

  val (replay19, reply19) = ReactionGameAction(
    6,
    replay18,
    ujson.Obj(
      "actionType" -> "addOrder",
      "order" -> Order(OrderMarch, modifier = -1).toJson,
      "houseType" -> "kraken",
      "tileNumber" -> 12
    )
  )

  Files.write(Paths.get("saves/forIntegration/ReplayAllOrders.json"), replay19.toJsonString.getBytes(StandardCharsets.UTF_8))
}
