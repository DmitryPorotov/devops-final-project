package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, JsonParsableAction, PlayerAction}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseChooseHouseCardAfterLion5, SubPhaseGetTidesOfBattleCards}
import ujson.Value

case class ActionResolveCardLion5(
                                   gameState: GameState,
                                   houseType: HouseType,
                                   doCancelCard: Boolean,
                                 ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat, _) = CardResolveBeforeCombat.validateAndGetCombatAndCardPhase(
      gameState.subPhase,
      houseType,
      gameState.combat,
      gameState.powerTokens(HouseType.Kraken)
    )
    
    if !doCancelCard 
    then return gameState.copy(
      subPhase = SubPhaseGetTidesOfBattleCards(Seq(gameState.combat.attackerHouse, gameState.combat.defenderHouse)),
      combat = updatedCombat
    )
      
    
    val opponentHouseType =
      if isAttackerAction
      then gameState.combat.defenderHouse
      else gameState.combat.attackerHouse

    val opponentCardCode =
      if isAttackerAction
      then updatedCombat.defenderCard.code
      else updatedCombat.attackerCard.code

    val updatedPhase =
      if hasCardsLeft(opponentHouseType)
      then SubPhaseChooseHouseCardAfterLion5(opponentHouseType, opponentCardCode)
      else SubPhaseGetTidesOfBattleCards(Seq(gameState.combat.attackerHouse, gameState.combat.defenderHouse))

    gameState.copy(
      subPhase = updatedPhase,
      combat =
        if isAttackerAction
        then updatedCombat.copy(defenderCard = null)
        else updatedCombat.copy(attackerCard = null),
      discardedHouseCards = gameState.discardedHouseCards +
        (opponentHouseType -> gameState.discardedHouseCards(opponentHouseType).filter(_ != opponentCardCode))
    )
  }

  private def hasCardsLeft(houseType: HouseType): Boolean = {
    gameState.discardedHouseCards.getOrElse(houseType, Seq()).size < 6
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardLion5",
    "houseType" -> ujson.Str(houseType.toString),
    "doCancelCard" -> doCancelCard
  )
}

object ActionResolveCardLion5 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardLion5 =
    ActionResolveCardLion5(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("doCancelCard").bool
    )
}