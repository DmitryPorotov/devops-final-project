package assetsForTests.gameStates

import assetsForTests.gameStates.OneOrderLeftToAdd.gameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitFootmen, MilitaryUnitKnights, MilitaryUnitPowerToken}
import fwc.game.houses.{HousePufferfish, HouseRose}
import fwc.game.phases.actionSubPhases.SubPhaseResolveMarchOrder
import fwc.game.planningPhase.{Order, OrderMarch}
import fwc.gameSaving.actions.planning.ActionAddOrder

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object ResolveMarchNoCombat extends App {
  val initialGameState = fwc.game.initializeGameState()
  var gameState = initialGameState.copy(
    subPhase = SubPhaseResolveMarchOrder(
      HousePufferfish
    ),
    armies = initialGameState.armies + (
      54 -> (
        initialGameState.armies(54)
        :+ MilitaryUnit(
          HousePufferfish,
          MilitaryUnitFootmen
          )
        :+ MilitaryUnit(
          HousePufferfish,
          MilitaryUnitKnights
        )
      )
    )
    + (
      52 -> Seq(
        MilitaryUnit(
          HouseRose,
          MilitaryUnitPowerToken
        )
      )
    ),
    placedOrders = initialGameState.placedOrders.placeOrder(HousePufferfish, 54, Order(OrderMarch), 2),
    availableOrders = initialGameState.availableOrders.useOrder(HousePufferfish, Order(OrderMarch))
  )

  Files.write(Paths.get("saves/forUnitTests/ResolveMarchNoCombat.json"), gameState.toJsonString.getBytes(StandardCharsets.UTF_8))
}
