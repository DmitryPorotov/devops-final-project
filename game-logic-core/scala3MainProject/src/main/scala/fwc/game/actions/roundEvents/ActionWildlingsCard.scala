package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsCard
import ujson.Value

case class ActionWildlingsCard(
                                gameState: GameState
                              ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
//    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsCard]
//    then throw new ActionException("Wrong phase")

    WildlingsCards.resolveCard(gameState).copy(
      boardCards = gameState.boardCards.copy(
        wildlings = gameState.boardCards.wildlings.tail :+ gameState.boardCards.wildlings.head
      )
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsCard",
  )
}

object ActionWildlingsCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsCard =
    ActionWildlingsCard(gameState)
}