package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitGarrison, MilitaryUnitPowerToken, TileNumber, TrackThrone}
import fwc.game.eventsPhase.Supplies
import fwc.game.houses.HouseType
import fwc.game.phases.PhaseRoundEvents
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.phases.roundEventsSubPhases.SubPhaseDisbandUnit
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionDisbandUnitDueToSupplies(
                               gameState: GameState,
                               houseType: HouseType,
                               tileNumber: TileNumber,
                               unit: MilitaryUnit,
                               nextStep: UnitDisbandNextStepType
                             ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseDisbandUnit]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseDisbandUnit]

    if currentPhase.nextStep == UnitDisbandNextStepCombatCleanUp || currentPhase.mainPhase != PhaseRoundEvents
    then throw new ActionException("Wrong phase")

    if currentPhase.houseType != houseType
    then throw new ActionException("Wrong house")

    val toConsolidate = Supplies.findArmiesToConsolidate(gameState.armies, gameState.supplies, houseType)

    if toConsolidate(houseType).isEmpty
    then throw new ActionException("Nothing to disband")

    if !toConsolidate(houseType).contains(tileNumber)
    then throw new ActionException(s"You should consolidate at tiles ${toConsolidate(houseType)} first")

    if unit.unitType.musteringPoints < 0
    then throw new ActionException(s"Can not disband ${unit.unitType}")

    val updatedArmies =
      gameState.armies.disbandMilitaryUnit(tileNumber, unit)

    val updatedToCon = Supplies.findArmiesToConsolidate(updatedArmies, gameState.supplies)
      .filter(_._2.nonEmpty)

    val newPhase =
      if updatedToCon.isEmpty
      then
        if nextStep == UnitDisbandNextStepDeck1
        then EventCards.fallThroughFromDeck1(gameState.tracks,gameState.boardCards)
        else if nextStep == UnitDisbandNextStepDeck2
          then EventCards.fallThroughFromDeck2(gameState.tracks,gameState.boardCards)
          else SubPhaseAddOrder(HouseType.getSeqOfAll)
      else if updatedToCon.contains(houseType)
        then SubPhaseDisbandUnit(houseType, currentPhase.nextStep)
        else SubPhaseDisbandUnit(
          Supplies.getHouseToConsolidate(updatedToCon, gameState.tracks(TrackThrone)),
          currentPhase.nextStep
        )

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "disbandUnitDueToSupplies",
    "houseType" -> houseType.toString,
    "tileNumber" -> tileNumber,
    "unit" -> unit.toJson,
    "nextStep" -> nextStep.toString
  )
}

object ActionDisbandUnitDueToSupplies extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionDisbandUnitDueToSupplies =
    ActionDisbandUnitDueToSupplies(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt,
      MilitaryUnit.fromJson(json("unit")),
      UnitDisbandNextStepType.fromString(json("nextStep").str)
    )
}
