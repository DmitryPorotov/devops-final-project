package fwc.gameSaving.actions.action

import enrichment.ExtSeq
import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitFootmen}
import fwc.game.houses.{HouseKraken, HouseType}
import fwc.gameSaving.actions.{Action, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardRose2(
                                   gameState: GameState,
                                   houseType: HouseType
                                 ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat, updatedPhase) = CardResolveBeforeCombat.validateAndGetCombatAndCardPhase(
      gameState.subPhase,
      houseType,
      gameState.combat,
      gameState.powerTokens(HouseKraken)
    )

    def deleteFootmen(army: Seq[MilitaryUnit]): Seq[MilitaryUnit] = {
      if army.exists(mu => mu.isDefeated && mu.unitType == MilitaryUnitFootmen)
      then
        army.deleteFirstMatch(
          MilitaryUnit(
            army.head.house,
            MilitaryUnitFootmen,
            true
          )
        )
      else
        army.deleteFirstMatch(
          MilitaryUnit(
            army.head.house,
            MilitaryUnitFootmen
          )
        )
    }

    gameState.copy(
      subPhase = updatedPhase,
      combat =
        if isAttackerAction
        then updatedCombat.copy(defenderArmy = deleteFootmen(updatedCombat.defenderArmy))
        else updatedCombat.copy(attackerArmy = deleteFootmen(updatedCombat.attackerArmy))
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardRose2",
    "houseType" -> ujson.Str(houseType.toString)
  )
}

object ActionResolveCardRose2 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardRose2 =
    ActionResolveCardRose2(
      gameState,
      HouseType.fromString(json("houseType").str)
    )
}