package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.actionPhase.CardCode
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseChooseHouseCardAfterLion5, SubPhaseGetTidesOfBattleCards}
import ujson.Value

case class ActionChooseHouseCardAfterLion5(
                                            gameState: GameState,
                                            houseType: HouseType,
                                            cardCode: CardCode
                                          ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseChooseHouseCardAfterLion5]
    then throw new ActionException("Wrong phase")

    if (houseType == HouseType.Lion)
      || (gameState.combat.attackerHouse != houseType
      && gameState.combat.defenderHouse != houseType)
    then throw new ActionException("Wrong house")

    if cardCode == gameState.subPhase.asInstanceOf[SubPhaseChooseHouseCardAfterLion5].bannedCardCode
    then throw new ActionException("Cant use the same card")

    val discardedHouseCards: Seq[CardCode] = gameState.discardedHouseCards(houseType)

    val houseCardOpt = gameRules.houseCards.find(hc => hc.house == houseType && hc.code == cardCode)
    val houseCard =
      if houseCardOpt.nonEmpty
      then houseCardOpt.head
      else throw new ActionException(s"Unknown house card code $cardCode")

    if discardedHouseCards.contains(cardCode)
    then throw new ActionException(s"The card ${houseCard.name} is discarded")

    val isAttackerAction = gameState.combat.defenderHouse == HouseType.Lion

    val updatedDiscardedForHouse = gameState.discardedHouseCards(houseType) :+ cardCode

    val updatedPhase = CombatCommon.getImmediatelyResolvableCardSubPhase(
      houseCard,
      gameState.powerTokens(HouseType.Kraken)
    )

    gameState.copy(
      subPhase =
        if updatedPhase != null
        then updatedPhase
        else SubPhaseGetTidesOfBattleCards(Seq(gameState.combat.attackerHouse, gameState.combat.defenderHouse)),
      discardedHouseCards = gameState.discardedHouseCards + (houseType -> updatedDiscardedForHouse),
      combat = 
        if isAttackerAction
        then gameState.combat.copy(attackerCard = houseCard)
        else gameState.combat.copy(defenderCard = houseCard)
    )

  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "chooseHouseCardAfterLion5",
    "houseType" -> ujson.Str(houseType.toString),
    "cardCode" -> ujson.Num(cardCode)
  )
}

object ActionChooseHouseCardAfterLion5 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionChooseHouseCardAfterLion5 =
    ActionChooseHouseCardAfterLion5(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("cardCode").num.toInt
    )
}