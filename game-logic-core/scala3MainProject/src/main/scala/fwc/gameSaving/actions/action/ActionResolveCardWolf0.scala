package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.{MilitaryUnit, MilitaryUnitGarrison, MilitaryUnitPowerToken, MilitaryUnitSiegeEngines, TileNumber}
import fwc.game.eventsPhase.{Mustering, Supplies}
import fwc.game.houses.HouseType
import fwc.game.phases.PhaseAction
import fwc.game.phases.roundEventsSubPhases.SubPhaseDisbandUnit
import fwc.game.planningPhase.OrderMarch
import fwc.gameSaving.actions.roundEvents.UnitDisbandNextStepCombatCleanUp
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardWolf0(
                                   gameState: GameState,
                                   houseType: HouseType,
                                   targetTileNumber: TileNumber
                                 ) extends Action(gameState)
  with PlayerAction(houseType)
  with MarchRetreatTrait(gameState, houseType)
  with JsonSerializable {

  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat) = CardResolve.validateAndGetCombat(gameState.subPhase, houseType, gameState.combat)

    if !gameState.combat.winnerCard.exists(_.isWolf0)
    then throw new ActionException("This phase is for wolf 0")

    val possibleRetreatTilesNumbers = getAllNeighboursBySea(gameState.combat.defenderTileNum)
      .filter(tn =>
        tn != gameState.combat.defenderTileNum
        &&
        (if isAttackerAction
        then tn != gameState.combat.attackerTileNum
        else true)
      )

    val armyToMove =
      if isAttackerAction
      then updatedCombat.defenderArmy
        .filter(mu =>
          !mu.isDefeated
            && mu.unitType.canRetreat
        )
        .map(_.copy(isDefeated = true))
      else updatedCombat.attackerArmy
        .filter(_.unitType.canRetreat)
        .map(_.copy(isDefeated = true))

    val possibleRetreatTilesWithLosses = possibleRetreatTilesNumbers.foldLeft(Map[TileNumber, Int]())(
      (acc, tn) =>
        val armyAtTile: Seq[MilitaryUnit] = gameState.armies.getOrElse(tn, Seq())
        if armyAtTile.isEmpty
        then acc + (tn -> 0)
        else
          if armyAtTile.head.house != gameState.combat.loser.head
          then acc
          else
            if armyAtTile.size == 1 && (armyAtTile.head.unitType == MilitaryUnitGarrison || armyAtTile.head.unitType == MilitaryUnitPowerToken)
            then acc + (tn -> 0)
            else acc + (tn -> countPossibleLoses(tn, armyToMove))
    )

    val minLosses = possibleRetreatTilesWithLosses.foldLeft(Int.MaxValue)(
      (acc, cur) =>
        if cur._2 < acc
        then cur._2
        else acc
    )

    val tilesWithMinLosses = possibleRetreatTilesWithLosses.foldLeft(Seq[Int]())(
      (acc, cur) =>
        if cur._2 == minLosses
        then acc :+ cur._1
        else acc
    )

    val updatedArmies =
      if tilesWithMinLosses.contains(targetTileNumber)
      then gameState.armies + (targetTileNumber -> (gameState.armies.getOrElse(targetTileNumber, Seq()) ++ armyToMove))
      else throw new ActionException(s"This tile will result in higher losses then $tilesWithMinLosses")

    val newPhase =
      if minLosses > 0
      then SubPhaseDisbandUnit(gameState.combat.loser.head, UnitDisbandNextStepCombatCleanUp, PhaseAction)
      else NextOrderFinder.nextSubPhase(gameState, OrderMarch, gameState.combat.winner.head)

    gameState.copy(
      armies = updatedArmies,
      combat = updatedCombat,
      subPhase = newPhase
    )
  }

  private def countPossibleLoses(tileNumber: TileNumber, armyToMove: Seq[MilitaryUnit]): Int = {
    val newArmies = gameState.armies + (tileNumber -> (gameState.armies(tileNumber) ++ armyToMove))

    val toConsolidate = Supplies.findArmiesToConsolidate(newArmies, gameState.supplies, gameState.combat.loser.head)
    if toConsolidate(armyToMove.head.house).nonEmpty
    then 1 //todo count properly somehow
    else 0
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardWolf0",
    "houseType" -> ujson.Str(houseType.toString),
    "targetTileNumber" -> targetTileNumber
  )
}

object ActionResolveCardWolf0 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardWolf0 =
    ActionResolveCardWolf0(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("targetTileNumber").num.toInt
    )
}