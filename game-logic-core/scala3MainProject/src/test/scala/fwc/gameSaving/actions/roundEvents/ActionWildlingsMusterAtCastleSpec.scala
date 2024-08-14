package fwc.gameSaving.actions.roundEvents

import fwc.game.board.{MilitaryUnit, MilitaryUnitType}
import fwc.game.houses.HouseType
import fwc.gameSaving.actions.Action
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class ActionWildlingsMusterAtCastleSpec extends AnyFlatSpec with should.Matchers {
  "ActionWildlingsMusterAtCastle" should "be parsed from json" in {
    val gameState = fwc.game.initializeGameState()
    val action = ActionWildlingsMusterAtCastle(
      gameState,
      houseType = HouseType.Moose,
      sourceTile = 3,
      targetUnits = Seq((
        3,
        false,
        MilitaryUnit(
          HouseType.Moose,
          MilitaryUnitType.Footmen
        )
      ))
    )
    val jsonStr = action.toJsonString

    val act2 = Action.fromJson(gameState, ujson.read(jsonStr))
    val a = 0
  }
}
