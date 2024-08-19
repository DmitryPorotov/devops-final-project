package assetsForTests.gameStates

import assetsForTests.gameStates.OneOrderLeftToAdd.gameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitType}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveMarchOrder
import fwc.game.planningPhase.{Order, OrderType}
import fwc.gameSaving.actions.planning.ActionAddOrder

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object ResolveMarchNoCombat extends App {
  val initialGameState = fwc.game.initializeGameState()
  var gameState = initialGameState.copy(
    subPhase = SubPhaseResolveMarchOrder(
      HouseType.PufferFish
    ),
    armies = initialGameState.armies + (
      54 -> (
        initialGameState.armies(54)
        :+ MilitaryUnit(
          HouseType.PufferFish,
          MilitaryUnitType.Footmen
          )
        :+ MilitaryUnit(
          HouseType.PufferFish,
          MilitaryUnitType.Knights
        )
      )
    )
    + (
      52 -> Seq(
        MilitaryUnit(
          HouseType.Rose,
          MilitaryUnitType.PowerToken
        )
      )
    ),
    placedOrders = initialGameState.placedOrders.placeOrder(HouseType.PufferFish, 54, Order(OrderType.March), 2),
    availableOrders = initialGameState.availableOrders.useOrder(HouseType.PufferFish, Order(OrderType.March))
  )

  Files.write(Paths.get("saves/forUnitTests/ResolveMarchNoCombat.json"), gameState.toJsonString.getBytes(StandardCharsets.UTF_8))
}
