package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.MilitaryUnit
import fwc.game.eventsPhase.Supplies
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseDisbandUnit
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value
import enrichment.ExtSeq
import fwc.game.phases.PhaseAction
import fwc.game.planningPhase.OrderType
import fwc.gameSaving.actions.roundEvents.UnitDisbandNextStepCombatCleanUp

case class ActionDisbandUnitsAfterCombat(
                                          gameState: GameState,
                                          houseType: HouseType,
                                          unit: MilitaryUnit
                                        ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseDisbandUnit]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseDisbandUnit]
    
    if currentPhase.nextStep != UnitDisbandNextStepCombatCleanUp || currentPhase.mainPhase != PhaseAction
    then throw new ActionException("Wrong phase")

    if currentPhase.houseType != houseType
    then throw new ActionException("Wrong house")

    val toConsolidate = Supplies.findArmiesToConsolidate(gameState.armies, gameState.supplies, houseType)

    if toConsolidate(houseType).isEmpty
    then throw new ActionException("Nothing to disband")

    val tileNumber = toConsolidate(houseType).head

    val updatedArmies =
      if !gameState.armies.getOrElse(tileNumber, Seq()).contains(unit)
      then throw new ActionException("No such unit at the tile")
      else
        val army = gameState.armies(tileNumber).deleteFirstMatch(unit)
        gameState.armies + (tileNumber -> army)

    val toConsolidate2 = Supplies.findArmiesToConsolidate(updatedArmies, gameState.supplies, houseType)

    val newPhase =
      if toConsolidate2(houseType).isEmpty
      then NextOrderFinder.nextSubPhase(gameState, OrderType.OrderMarch, gameState.combat.winner.head)
      else SubPhaseDisbandUnit(houseType, UnitDisbandNextStepCombatCleanUp, PhaseAction)

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "disbandUnitsAfterCombat",
    "houseType" -> ujson.Str(houseType.toString),
    "unit" -> unit.toJson
  )
}

object ActionDisbandUnitsAfterCombat extends JsonParsableAction{
  override def fromJson(gameState: GameState, json: Value): ActionDisbandUnitsAfterCombat =
    ActionDisbandUnitsAfterCombat(
      gameState,
      HouseType.fromString(json("houseType").str),
      MilitaryUnit.fromJson(json("unit"))
    )
}
