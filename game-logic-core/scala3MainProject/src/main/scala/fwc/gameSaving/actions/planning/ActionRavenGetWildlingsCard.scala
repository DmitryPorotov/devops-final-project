package fwc.gameSaving.actions.planning

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.eventsPhase.cards.TidesOfBattleDeckEmptyException
import fwc.game.phases.actionSubPhases.{SubPhaseGetTidesOfBattleCards, SubPhaseRefreshTidesOfBattleDeck}
import fwc.game.phases.planningSubPhases
import fwc.game.phases.planningSubPhases.SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom
import fwc.gameSaving.actions.{Action, JsonParsableAction}
import ujson.Value

case class ActionRavenGetWildlingsCard(
                                        gameState: GameState,
                                        isRandom: Boolean
                                      ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
    gameState.copy(
      subPhase = planningSubPhases.SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(gameState.tracks.ravenOwner)
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "ravenGetWildlingsCard",
    "isRandom" -> isRandom
  )

}

object ActionRavenGetWildlingsCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionRavenGetWildlingsCard =
    ActionRavenGetWildlingsCard(
      gameState,
      json("isRandom").bool
    )
}
