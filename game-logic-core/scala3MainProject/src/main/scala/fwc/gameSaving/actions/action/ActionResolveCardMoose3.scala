package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.{CardCode, isValid}
import fwc.game.houses.HouseType
import fwc.game.planningPhase.OrderMarch
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardMoose3(
                                    gameState: GameState,
                                    houseType: HouseType,
                                    cardCode: CardCode
                                  ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {

    val (_, updatedCombat) = CardResolve.validateAndGetCombat(gameState.subPhase, houseType, gameState.combat)

    if !gameState.combat.winnerCard.exists(_.isLion1)
    then throw new ActionException("This phase is for moose 2")

    if !cardCode.isValid
    then throw new ActionException("Card code is invalid")

    val loserHouse = gameState.combat.loser.head

    val updatedDiscardedHouseCards =
      if cardCode < 0
      then gameState.discardedHouseCards
      else
        val loserDisCards: Seq[Int] = gameState.discardedHouseCards.getOrElse(loserHouse, Seq[Int]())
        if loserDisCards.contains(cardCode)
        then throw new ActionException("This card is already discarded")
        else gameState.discardedHouseCards + (loserHouse -> (gameState.discardedHouseCards(loserHouse) :+ cardCode))

    gameState.copy(
      subPhase = NextOrderFinder.nextSubPhase(gameState, OrderMarch, gameState.combat.attackerHouse),
      combat = null,
      discardedHouseCards = updatedDiscardedHouseCards.resetDecksAfterCombat(updatedCombat)
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardMoose3",
    "houseType" -> houseType.toString,
    "cardCode" -> cardCode
  )
}

object ActionResolveCardMoose3 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardMoose3 =
    ActionResolveCardMoose3(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("cardCode").num.toInt
    )
}
