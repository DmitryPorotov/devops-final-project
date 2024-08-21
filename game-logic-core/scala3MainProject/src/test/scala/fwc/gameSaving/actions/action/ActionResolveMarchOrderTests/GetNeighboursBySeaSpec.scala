package fwc.gameSaving.actions.action.ActionResolveMarchOrderTests

import fwc.game.GameState
import fwc.game.actions.action.ActionResolveMarchOrder
import fwc.game.board.{MilitaryUnit, MilitaryUnitType}
import fwc.game.houses.HouseType
import fwc.gameLoading
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class GetNeighboursBySeaSpec extends AnyFlatSpec with should.Matchers {
  "GetNeighboursBySea method" should "find neighbours" in {
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
    val n = action.getAllNeighboursBySea(16)
    assert(n.size == 6)
    assert(n.contains(11))
    assert(n.contains(12))
    assert(n.contains(16))
    assert(n.contains(18))
    assert(n.contains(24))
    assert(n.contains(33))
  }
}
