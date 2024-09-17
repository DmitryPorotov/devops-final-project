package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.MilitaryUnit
import fwc.game.eventsPhase.Supplies
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseDisbandUnit
import ujson.Value
import enrichment.ExtSeq
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.UnitDisbandNextStepCombatCleanUp
import fwc.game.phases.MainPhase
import fwc.game.phases.actionSubPhases.SubPhaseCleanUpAfterCombat
import fwc.game.planningPhase.OrderType

case class ActionDisbandUnitsAfterCombat(
                                          gameState: GameState,
                                          houseType: HouseType,
                                          unit: MilitaryUnit
                                        ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseDisbandUnit]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseDisbandUnit]
    
    if currentPhase.nextStep != UnitDisbandNextStepCombatCleanUp || currentPhase.mainPhase != MainPhase.Action
    then throw new ActionException("Wrong phase")

    if currentPhase.houseType != houseType
    then throw new ActionException("Wrong house")

    val toConsolidate = Supplies.findArmiesToConsolidate(gameState.armies, gameState.supplies, houseType)

    if toConsolidate(houseType).isEmpty
    then throw new ActionException("Nothing to disband")

    val tileNumber = toConsolidate(houseType).head

    val updatedArmies =
      val army = gameState.armies(tileNumber).deleteFirstMatch(getUnitPrioritizeFullMatch(gameState.armies.getOrElse(tileNumber, Seq()), unit))
      gameState.armies + (tileNumber -> army)

    val toConsolidate2 = Supplies.findArmiesToConsolidate(updatedArmies, gameState.supplies, houseType)

    val doNotDisbandAnymore = toConsolidate2(houseType).isEmpty

    val newPhase =
      if doNotDisbandAnymore
      then SubPhaseCleanUpAfterCombat(Seq(gameState.combat.attackerHouse, gameState.combat.defenderHouse))
      else SubPhaseDisbandUnit(houseType, UnitDisbandNextStepCombatCleanUp, MainPhase.Action)

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies,
    )
  }

  private def getUnitPrioritizeFullMatch(army: Seq[MilitaryUnit], unit: MilitaryUnit): MilitaryUnit = {
    if army.contains(unit)
    then unit
    else
      val unitChangedDefeated = unit.copy(isDefeated = !unit.isDefeated)
      if army.contains(unitChangedDefeated)
      then unitChangedDefeated
      else throw new ActionException("No such unit at the tile")
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
