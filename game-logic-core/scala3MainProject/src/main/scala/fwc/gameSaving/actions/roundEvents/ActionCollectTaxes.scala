package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.eventsPhase.Taxes
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction}
import ujson.Value

case class ActionCollectTaxes(
                           gameState: GameState
                         ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {

    val updatedPowerTokens = Taxes.collectTaxes(gameState.armies, gameState.powerTokens)
    
    gameState.copy(
      subPhase = EventCards.getPhaseForDeck3Card(gameState.boardCards.roundEvents3.head, gameState.tracks.steelBladeOwner),
      powerTokens = updatedPowerTokens
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "collectTaxes",
  )
}

object ActionCollectTaxes extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionCollectTaxes =
    ActionCollectTaxes(gameState)
}