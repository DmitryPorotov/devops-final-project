package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.phases.roundEventsSubPhases.{SubPhaseGetWildlingsCard, SubPhaseSetWildlingsCard}
import fwc.game.{GameState, gameRules}
import fwc.gameSaving.actions.{Action, ActionException, ActionSetCard, JsonParsableAction}
import ujson.Value

case class ActionSetWildlingsCard(
                                   gameState: GameState,
                                   wildlingsCardCode: Int
                                 )
  extends Action(gameState) with JsonSerializable with ActionSetCard {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseSetWildlingsCard]
    then throw new ActionException("Wrong phase")

    if wildlingsCardCode < 0 || wildlingsCardCode > 8
    then  throw new ActionException("Invalid wildlings card code")

    gameState.copy(
      subPhase = gameState.subPhase.asInstanceOf[SubPhaseSetWildlingsCard]
        .subPhaseWildlingsCard.copy(cardCode = wildlingsCardCode),
      boardCards =
        gameState.boardCards.copy(
          wildlings = gameState.boardCards.wildlings
            prepended
            gameRules.boardCards.wildlings.find(_.code == wildlingsCardCode).head
        )
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "setWildlingsCard",
    "wildlingsCardCode" -> wildlingsCardCode
  )
}

object ActionSetWildlingsCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionSetWildlingsCard =
    ActionSetWildlingsCard(
      gameState,
      json("wildlingsCardCode").num.toInt
    )
}