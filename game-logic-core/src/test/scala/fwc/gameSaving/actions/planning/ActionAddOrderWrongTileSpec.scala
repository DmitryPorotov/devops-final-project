package fwc.gameSaving.actions.planning

import fwc.game.houses.{HouseKraken, HouseWolf}
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.planningPhase.{Order, OrderMarch}
import fwc.game.{FWCException, GameState}
import fwc.gameLoading
import fwc.gameSaving.actions.ActionException
import fwc.gameSaving.actions.planning.ActionAddOrder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ActionAddOrderWrongTileSpec extends AnyFlatSpec with should.Matchers {
  "ActionAddOrder " should "should throw when trying to add order to wrong tile" in {
    val json = gameLoading.readJson("saves/forUnitTests/initGameState.json")
    val gameState = GameState.fromJson(json)

    assertThrows[ActionException](ActionAddOrder(
      gameState,
      HouseWolf,
      Order(
        OrderMarch
      ),

      5
    ).doAction())

    assertThrows[FWCException](ActionAddOrder(
      gameState,
      HouseKraken,
      Order(
        OrderMarch
      ),
      3
    ).doAction())

    assertThrows[FWCException](ActionAddOrder(
      gameState,
      HouseKraken,
      Order(
        OrderMarch,
        true,
        1
      ),
      16
    ).doAction(), "Kraken should not have enough stars")
  }
}
