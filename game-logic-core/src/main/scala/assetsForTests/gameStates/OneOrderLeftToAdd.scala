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
  var gameState = ActionAddOrder(initialGameState, HouseLion, orderDefend, 21).doAction()
  gameState = ActionAddOrder(gameState, HouseLion, orderDefend, 22).doAction()
  gameState = ActionAddOrder(gameState, HouseLion, orderMarch, 27).doAction()

  gameState = ActionAddOrder(gameState, HouseKraken, Order(OrderConsolidatePower), 15).doAction()
  gameState = ActionAddOrder(gameState, HouseKraken, orderDefend, 16).doAction()
  gameState = ActionAddOrder(gameState, HouseKraken, orderDefend, 17).doAction()
  gameState = ActionAddOrder(gameState, HouseKraken, orderMarch, 12).doAction()

  gameState = ActionAddOrder(gameState, HousePufferfish, orderMarch, 50).doAction()
  gameState = ActionAddOrder(gameState, HousePufferfish, orderDefend, 54).doAction()
  gameState = ActionAddOrder(gameState, HousePufferfish, orderDefend, 55).doAction()

  gameState = ActionAddOrder(gameState, HouseWolf, orderDefend, 3).doAction()
  gameState = ActionAddOrder(gameState, HouseWolf, orderDefend, 7).doAction()

  gameState = ActionAddOrder(gameState, HouseMoose, orderDefend, 30).doAction()
  gameState = ActionAddOrder(gameState, HouseMoose, orderDefend, 31).doAction()
  gameState = ActionAddOrder(gameState, HouseMoose, orderMarch, 40).doAction()

  gameState = ActionAddOrder(gameState, HouseRose, orderMarch, 38).doAction()
  gameState = ActionAddOrder(gameState, HouseRose, orderDefend, 41).doAction()
  gameState = ActionAddOrder(gameState, HouseRose, orderDefend, 44).doAction()


   Files.write(Paths.get("saves/forUnitTests/OneOrderLeftToAdd.json"), gameState.toJsonString.getBytes(StandardCharsets.UTF_8))
}
