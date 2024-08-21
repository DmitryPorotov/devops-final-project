package fwc.gameSaving.actions.action.ActionResolveMarchOrderTests

import fwc.game.GameState
import fwc.game.actions.action.ActionResolveMarchOrder
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitType}
import fwc.game.houses.HouseType
import fwc.gameLoading
import org.scalatest.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class HasPathSpec extends AnyFlatSpec with should.Matchers {
  "HasPath method" should "check if path exists" in {
    val json = gameLoading.readJson("saves/forUnitTests/initGameState.json")
    val gameState = GameState.fromJson(json)

    val newGameState = gameState.copy(
      armies =
        gameState.armies + (10 -> Seq(MilitaryUnit(
          HouseType.Kraken,
          MilitaryUnitType.Ships
        )))
    )

    val action = ActionResolveMarchOrder(newGameState, HouseType.Kraken, 16, Map())
    val n = action.hasPath(16, 33)
    val a = 0
  }
}
