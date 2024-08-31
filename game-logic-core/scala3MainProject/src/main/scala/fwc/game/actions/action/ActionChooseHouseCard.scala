package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.actionPhase.Combat
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.{GameState, gameRules, houses}
import fwc.game.board.MilitaryUnit
import fwc.game.houses.*
import fwc.game.phases.{SubPhase, SubPhaseSingleHouse}
import fwc.game.phases.actionSubPhases.{SubPhaseChooseHouseCard, SubPhaseGetTidesOfBattleCards, SubPhaseResolveHouseCard}
import fwc.gameLoading.HouseCard
import ujson.Value

case class ActionChooseHouseCard(
                                 gameState: GameState,
                                 houseType: HouseType,
                                 cardCode: Int
                               ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseChooseHouseCard]
    then throw new ActionException("Wrong phase")

    if gameState.combat.attackerHouse != houseType
      && gameState.combat.defenderHouse != houseType
    then throw new ActionException("Wrong house")

    val houseCardOpt = gameRules.houseCards.find(hc => hc.house == houseType && hc.code == cardCode)
    val houseCard =
      if houseCardOpt.nonEmpty
      then houseCardOpt.head
      else throw new ActionException(s"Unknown house card code $cardCode")

    val discardedCardsForHouse: Seq[Int] = gameState.discardedHouseCards.getOrElse(houseType, Seq())
    if discardedCardsForHouse.contains(cardCode)
    then throw new ActionException(s"The card ${houseCard.name} is discarded")

    val updatedCombat = gameState.combat.addHouseCard(houseCard)

    val newPhase =
      if updatedCombat.attackerCard == null 
      then gameState.subPhase.asInstanceOf[SubPhaseChooseHouseCard].copy(
        houseTypes = Seq(updatedCombat.attackerHouse)
      )
      else if  updatedCombat.defenderCard == null 
      then  gameState.subPhase.asInstanceOf[SubPhaseChooseHouseCard].copy(
          houseTypes = Seq(updatedCombat.defenderHouse)
        )
      else getNewSubPhase(updatedCombat)

    gameState.copy(
      subPhase = newPhase,
      combat = updatedCombat
    )
  }

  private def getNewSubPhase(combat: Combat): SubPhase = {

    val attackerP = CombatCommon.getImmediatelyResolvableCardSubPhase(
      combat.attackerCard,
      gameState.powerTokens(combat.attackerHouse)
    )
    val defenderP = CombatCommon.getImmediatelyResolvableCardSubPhase(
      combat.defenderCard,
      gameState.powerTokens(combat.defenderHouse)
    )

    if attackerP != null && attackerP.isInstanceOf[SubPhaseResolveHouseCard] && attackerP.asInstanceOf[SubPhaseResolveHouseCard].cardCode == 5 && attackerP.houseType == HouseType.Lion
    then return attackerP

    if defenderP != null && attackerP.isInstanceOf[SubPhaseResolveHouseCard] && defenderP.asInstanceOf[SubPhaseResolveHouseCard].cardCode == 5 && defenderP.houseType == HouseType.Lion
    then return defenderP

    if attackerP != null
    then return attackerP

    if defenderP != null
    then return defenderP

    SubPhaseGetTidesOfBattleCards(Seq(combat.attackerHouse, combat.defenderHouse))
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "chooseHouseCard",
    "houseType" -> ujson.Str(houseType.toString),
    "cardCode" -> ujson.Num(cardCode)
  )
}

object ActionChooseHouseCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionChooseHouseCard =
    ActionChooseHouseCard(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("cardCode").num.toInt
    )
}
