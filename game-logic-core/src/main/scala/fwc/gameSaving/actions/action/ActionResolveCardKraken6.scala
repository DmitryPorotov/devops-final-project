package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.TrackType
import fwc.game.houses.{HouseKraken, HouseType}
import fwc.game.phases.actionSubPhases.SubPhaseResolveHouseCard
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveCardKraken6(
                                     gameState: GameState,
                                     houseType: HouseType,
                                     newCardCode: Int = -1
                                   ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    val (isAttackerAction, updatedCombat, updatedPhase) = CardResolveBeforeCombat.validateAndGetCombatAndCardPhase(
      gameState.subPhase,
      houseType,
      gameState.combat,
      gameState.powerTokens(HouseKraken)
    )

    if newCardCode < 0
    then gameState.copy(
      subPhase = updatedPhase,
      combat = updatedCombat
    )
    else
      if gameState.powerTokens(HouseKraken) < 2
      then throw new ActionException("House Kraken has not enough power tokens.")

      val discardedKrakenCards: Seq[Int] = gameState.discardedHouseCards.getOrElse(HouseKraken, Seq())
      val newKrakenCard = gameRules.houseCards.find(hc => hc.house == HouseKraken && hc.code == newCardCode).head
      if discardedKrakenCards.contains(newCardCode)
      then throw new ActionException(s"House Kraken's card ${newKrakenCard.name} is discarded.")

      val updatedPowerTokens = gameState.powerTokens + (HouseKraken -> (gameState.powerTokens(HouseKraken) - 2))
      val updatedDisCards = gameState.discardedHouseCards + (HouseKraken -> (discardedKrakenCards :+ newCardCode))
      val updatedCombat2 =
        if isAttackerAction
        then updatedCombat.copy(attackerCard = newKrakenCard)
        else updatedCombat.copy(defenderCard = newKrakenCard)

      gameState.copy(
        subPhase = updatedPhase,
        combat = updatedCombat2,
        powerTokens = updatedPowerTokens,
        discardedHouseCards = updatedDisCards
      )
  }
  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveCardKraken6",
    "houseType" -> houseType.toString,
    "newCardCode" -> newCardCode
  )
}

object ActionResolveCardKraken6 extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveCardKraken6 =
    ActionResolveCardKraken6(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("newCardCode").num.toInt
    )
}

