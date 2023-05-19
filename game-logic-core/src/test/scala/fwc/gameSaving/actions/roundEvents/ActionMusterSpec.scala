package fwc.gameSaving.actions.roundEvents

import fwc.game.houses.HouseMoose
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class ActionMusterSpec extends AnyFlatSpec with should.Matchers {
  "ActionMuster" should "be parsed from json" in {
    val gameState = fwc.game.initializeGameState()
    val json = ujson.Obj(
      "houseType" -> "moose",
      "unitToMuster" -> ujson.Obj(
        "house" -> "moose",
        "type" -> "footmen"
      ),
      "fromTile" -> 3
    )
    val action = ActionMuster.fromJson(gameState, json)
    action.houseType == HouseMoose
  }
}
