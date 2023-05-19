package fwc.gameSaving.actions.action.ActionResolveMarchOrderTests

import fwc.game.GameState
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitShips}
import fwc.game.houses.HouseKraken
import fwc.gameLoading
import fwc.gameSaving.actions.action.ActionResolveMarchOrder
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
          HouseKraken,
          MilitaryUnitShips
        )))
    )

    val action = ActionResolveMarchOrder(newGameState, HouseKraken, 16, Map())
    val n = action.hasPath(16, 33)
    val a = 0
  }
}
