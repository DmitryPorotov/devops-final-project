package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.TileNumber
import fwc.game.eventsPhase.Supplies
import fwc.game.houses.HouseType
import fwc.game.phases.PhaseAction
import fwc.game.phases.actionSubPhases.{SubPhaseCleanUpAfterCombat, SubPhaseRetreatUnitsAfterBattle}
import fwc.game.phases.roundEventsSubPhases.SubPhaseDisbandUnit
import fwc.gameSaving.actions.roundEvents.UnitDisbandNextStepCombatCleanUp
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionRetreatUnitsAfterBattle(
                                          gameState: GameState,
                                          houseType: HouseType,
                                          targetTileNumber: TileNumber
                                        ) extends Action(gameState)
  with PlayerAction(houseType)
  with MarchRetreatTrait(gameState, houseType)
  with JsonSerializable {

  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseRetreatUnitsAfterBattle]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseRetreatUnitsAfterBattle].houseType != houseType
    then throw new ActionException("Wrong house")

    val updatedArmies =
      if houseType == gameState.combat.attackerHouse
      then gameState.armies + (
          gameState.combat.attackerTileNum ->
          (gameState.armies.getOrElse(gameState.combat.attackerTileNum, Seq())
          ++ gameState.combat.attackerArmy.view
          .filter(_.unitType.canRetreat)
          .map(_.copy(isDefeated = true))
          )
        )
      else
        val possibleTilesToRetreat = getAllNeighboursBySea(targetTileNumber).filter(
          tn =>
            tn != gameState.combat.attackerTileNum
            && tn != gameState.combat.defenderTileNum
            && !gameState.armies.getOrElse(tn, Seq()).exists(_.house != houseType)
        )
        if !possibleTilesToRetreat.contains(targetTileNumber)
        then throw new ActionException(s"Cannot retreat to ${gameRules.board(targetTileNumber).name}")
        gameState.armies + (
          targetTileNumber ->
            (gameState.armies.getOrElse(targetTileNumber, Seq())
              ++ gameState.combat.defenderArmy.view
              .filter(_.unitType.canRetreat)
              .map(_.copy(isDefeated = true))
            )
          )

    val toConsolidate = Supplies.findArmiesToConsolidate(updatedArmies, gameState.supplies, houseType)
    val newPhase =
      if toConsolidate(houseType).nonEmpty
      then SubPhaseDisbandUnit(gameState.combat.loser.head, UnitDisbandNextStepCombatCleanUp, PhaseAction)
      else SubPhaseCleanUpAfterCombat(Seq(gameState.combat.attackerHouse, gameState.combat.attackerHouse))

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "retreatUnitsAfterBattle",
    "houseType" -> ujson.Str(houseType.toString),
    "targetTileNumber" -> targetTileNumber
  )
}

object ActionRetreatUnitsAfterBattle extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionRetreatUnitsAfterBattle =
    ActionRetreatUnitsAfterBattle(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("targetTileNumber").num.toInt
    )
}