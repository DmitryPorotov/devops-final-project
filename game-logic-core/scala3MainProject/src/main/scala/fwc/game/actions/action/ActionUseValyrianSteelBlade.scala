package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.{CombatOutcome, ValyrianSteelBladeChoiceType}
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.board.DominanceTokenValyrianSword
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseCalculateCombatOutcome, SubPhaseChooseToUseValyrianSteelBlade, SubPhaseGetTidesOfBattleCards, SubPhaseSetTidesOfBattleCards}
import ujson.Value

case class ActionUseValyrianSteelBlade(
                                        gameState: GameState,
                                        houseType: HouseType,
                                        choice: ValyrianSteelBladeChoiceType
                                      ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseChooseToUseValyrianSteelBlade]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseChooseToUseValyrianSteelBlade].houseType != houseType
    then throw new ActionException("Wrong house")

    val isAttackerAction = CardResolve.isAttackerAction(houseType, gameState.combat)

    if gameState.tracks.steelBladeOwner != houseType
    then throw new ActionException(s"House $houseType has no Valyrian Steel Blade")

    if gameState.dominanceTokensUsage(DominanceTokenValyrianSword)
    then throw new ActionException(s"Valyrian Steel Blade was already used this round")

    val updatedUsage =
      if choice != ValyrianSteelBladeChoiceType.Nothing
      then gameState.dominanceTokensUsage + (DominanceTokenValyrianSword -> true)
      else gameState.dominanceTokensUsage

    val updatedCombat =
      if choice == ValyrianSteelBladeChoiceType.PlusOne
      then gameState.combat.copy(
        combatOutcome = CombatOutcome(
          if isAttackerAction then 1 else 0,
          if isAttackerAction then 0 else 1,
          None,
          0,
          0
        )
      )
      else if choice == ValyrianSteelBladeChoiceType.ChangeTOBCard
      then
        if isAttackerAction
        then gameState.combat.copy(attackerTidesOfBattle = null)
        else gameState.combat.copy(defenderTidesOfBattle = null)
      else gameState.combat

    val newPhase =
      if choice == ValyrianSteelBladeChoiceType.Nothing || choice == ValyrianSteelBladeChoiceType.PlusOne
      then SubPhaseCalculateCombatOutcome(Seq(updatedCombat.attackerHouse, updatedCombat.defenderHouse))
      else SubPhaseSetTidesOfBattleCards(
        Seq(updatedCombat.attackerHouse, updatedCombat.defenderHouse),
        attackerCard = if updatedCombat.attackerTidesOfBattle == null then None else Some(updatedCombat.attackerTidesOfBattle.code),
        defenderCard = if updatedCombat.defenderTidesOfBattle == null then None else Some(updatedCombat.defenderTidesOfBattle.code),
      )

    gameState.copy(
      subPhase = newPhase,
      dominanceTokensUsage = updatedUsage,
      combat = updatedCombat
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "useValyrianSteelBlade",
    "houseType" -> houseType.toString,
    "choice" -> choice.toString
  )
}

object ActionUseValyrianSteelBlade extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionUseValyrianSteelBlade =
    ActionUseValyrianSteelBlade(
      gameState,
      HouseType.fromString(json("houseType").str),
      ValyrianSteelBladeChoiceType.fromString(json("choice").str)
    )
}
