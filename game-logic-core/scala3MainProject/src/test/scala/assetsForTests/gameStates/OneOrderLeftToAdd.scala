package assetsForTests.gameStates

import fwc.game.houses.*
import fwc.game.planningPhase.{Order, OrderConsolidatePower, OrderDefend, OrderMarch}
import fwc.gameSaving.actions.planning.ActionAddOrder

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object OneOrderLeftToAdd extends App {
  val initialGameState = fwc.game.initializeGameState()
  val orderDefend = Order(
    OrderDefend,
    modifier = 1
  )
  val orderMarch = Order(OrderMarch)
  var gameState = ActionAddOrder(initialGameState, HouseType.Lion, orderDefend, 21).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Lion, orderDefend, 22).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Lion, orderMarch, 27).doAction()

  gameState = ActionAddOrder(gameState, HouseType.Kraken, Order(OrderConsolidatePower), 15).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Kraken, orderDefend, 16).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Kraken, orderDefend, 17).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Kraken, orderMarch, 12).doAction()

  gameState = ActionAddOrder(gameState, HouseType.PufferFish, orderMarch, 50).doAction()
  gameState = ActionAddOrder(gameState, HouseType.PufferFish, orderDefend, 54).doAction()
  gameState = ActionAddOrder(gameState, HouseType.PufferFish, orderDefend, 55).doAction()

  gameState = ActionAddOrder(gameState, HouseType.Wolf, orderDefend, 3).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Wolf, orderDefend, 7).doAction()

  gameState = ActionAddOrder(gameState, HouseType.Moose, orderDefend, 30).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Moose, orderDefend, 31).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Moose, orderMarch, 40).doAction()

  gameState = ActionAddOrder(gameState, HouseType.Rose, orderMarch, 38).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Rose, orderDefend, 41).doAction()
  gameState = ActionAddOrder(gameState, HouseType.Rose, orderDefend, 44).doAction()


   Files.write(Paths.get("saves/forUnitTests/OneOrderLeftToAdd.json"), gameState.toJsonString.getBytes(StandardCharsets.UTF_8))
}
