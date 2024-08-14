package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.eventsPhase.cards.TidesOfBattleDeckEmptyException
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseCalculateCombatOutcome, SubPhaseChooseToUseValyrianSteelBlade, SubPhaseSetTidesOfBattleCards}
import fwc.gameSaving.actions.{Action, ActionException, ActionSetCard, JsonParsableAction}
import ujson.Value

case class ActionSetTidesOfBattleCards(
                                        gameState: GameState,
                                        attackerCardCode: Int,
                                        defenderCardCode: Int
                                      )
  extends Action(gameState) with JsonSerializable with ActionSetCard {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseSetTidesOfBattleCards]
    then throw new ActionException("Wrong phase")

    if attackerCardCode > 5 || defenderCardCode > 5 || attackerCardCode < 0 || defenderCardCode < 0
    then throw new ActionException("Wrong card code")

    val houseWithSteelBlade =
      if gameState.tracks.steelBladeOwner == gameState.combat.attackerHouse
      then Some(gameState.combat.attackerHouse)
      else
        if gameState.tracks.steelBladeOwner == gameState.combat.defenderHouse
        then Some(gameState.combat.defenderHouse)
        else None

    val newSubPhase =
      if houseWithSteelBlade.nonEmpty
      then SubPhaseChooseToUseValyrianSteelBlade(houseWithSteelBlade.head)
      else SubPhaseCalculateCombatOutcome()

    gameState.copy(
      subPhase = newSubPhase,
      combat = gameState.combat.copy(
        attackerTidesOfBattle = gameRules.boardCards.tidesOfBattle.find(_.code == attackerCardCode).head,
        defenderTidesOfBattle = gameRules.boardCards.tidesOfBattle.find(_.code == defenderCardCode).head
      )
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "setTidesOfBattleCards",
    "attackerCardCode" -> attackerCardCode,
    "defenderCardCode" -> defenderCardCode
  )
}

object ActionSetTidesOfBattleCards extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionSetTidesOfBattleCards =
    ActionSetTidesOfBattleCards(
      gameState,
      json("attackerCardCode").num.toInt,
      json("defenderCardCode").num.toInt
    )
}