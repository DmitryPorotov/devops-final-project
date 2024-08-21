package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitType}
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseCleanUpAfterCombat, SubPhaseKillUnitsAfterBattle, SubPhaseResolveHouseCard, SubPhaseRetreatUnitsAfterBattle}
import ujson.Value

case class ActionKillUnitsAfterBattle(
                                       gameState: GameState,
                                       houseType: HouseType,
                                       units: Seq[MilitaryUnit]
                                     ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseKillUnitsAfterBattle]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseKillUnitsAfterBattle].houseType != houseType
    then throw new ActionException("Wrong house")

    val numberOfUnitsToKill =
      if gameState.combat.attackerHouse == houseType
      then gameState.combat.combatOutcome.attackerUnitsToKill
      else gameState.combat.combatOutcome.defenderUnitsToKill

    if units.size != numberOfUnitsToKill
    then throw new ActionException(s"Wrong number of units to kill, needed $numberOfUnitsToKill got ${units.size}")

    units.foreach(mu =>
      if mu.isDefeated
      then throw new ActionException("Military unit to kill must not be defeated")
      if mu.house != houseType
      then throw new ActionException("Military unit to kill is from wrong house")
      if !mu.unitType.canBeMustered
      then throw new ActionException(s"Military unit to kill can't be ${mu.unitType}")
    )

    val isAttackerAction = CardResolve.isAttackerAction(houseType, gameState.combat)

    val armiesLeft = Armies.subtractArmies(
      if isAttackerAction then gameState.combat.attackerArmy else gameState.combat.defenderArmy,
      units
    )

    val loserHouse = gameState.combat.loser.head

    val winnerHasWolf0 = gameState.combat.winnerCard.exists(_.isWolf0)

    val possibleRetreatFunc =
        if armiesLeft.exists(
          mu =>
            !mu.isDefeated
            && !mu.unitType.canRetreat
        )
        then
          if winnerHasWolf0
          then SubPhaseResolveHouseCard(HouseType.Wolf, 0)
          else SubPhaseRetreatUnitsAfterBattle(loserHouse)
        else SubPhaseCleanUpAfterCombat(Seq(gameState.combat.attackerHouse, gameState.combat.defenderHouse))

    val newPhase: SubPhase =
      if isAttackerAction
      then
        if gameState.combat.combatOutcome.defenderUnitsToKill > 0
        then SubPhaseKillUnitsAfterBattle(gameState.combat.defenderHouse)
        else possibleRetreatFunc
      else
        if gameState.combat.combatOutcome.attackerUnitsToKill > 0
        then SubPhaseKillUnitsAfterBattle(gameState.combat.attackerHouse)
        else possibleRetreatFunc

    gameState.copy(
      subPhase = newPhase,
      combat =
        if isAttackerAction
        then gameState.combat.copy(
          attackerArmy = armiesLeft,
          combatOutcome = gameState.combat.combatOutcome.copy(
            attackerUnitsToKill = 0
          )
        )
        else gameState.combat.copy(
          defenderArmy = armiesLeft,
          combatOutcome = gameState.combat.combatOutcome.copy(
            defenderUnitsToKill = 0
          )
        )
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "killUnitsAfterBattle",
    "houseType" -> houseType.toString,
    "units" -> ujson.Arr.from(
      units.map(_.toJson)
    )
  )
}

object ActionKillUnitsAfterBattle extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionKillUnitsAfterBattle =
    ActionKillUnitsAfterBattle(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("units").arr.map(mu => MilitaryUnit.fromJson(mu)).toSeq
    )
}
