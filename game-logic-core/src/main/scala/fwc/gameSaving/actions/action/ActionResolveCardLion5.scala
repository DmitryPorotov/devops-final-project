package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.houses.{HouseKraken, HouseType}
import fwc.game.phases.actionSubPhases.{SubPhaseChooseHouseCardAfterLion5, SubPhaseGetTidesOfBattleCards}
import fwc.gameSaving.actions.{Action, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardLion5(
                                   gameState: GameState,
                                   houseType: HouseType
                                 ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat, _) = CardResolveBeforeCombat.validateAndGetCombatAndCardPhase(
      gameState.subPhase,
      houseType,
      gameState.combat,
      gameState.powerTokens(HouseKraken)
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
      else SubPhaseGetTidesOfBattleCards()

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

  def hasCardsLeft(houseType: HouseType): Boolean = {
    gameState.discardedHouseCards.getOrElse(houseType, Seq()).size < 6
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardLion5",
    "houseType" -> ujson.Str(houseType.toString)
  )
}

object ActionResolveCardLion5 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardLion5 =
    ActionResolveCardLion5(
      gameState,
      HouseType.fromString(json("houseType").str)
    )
}