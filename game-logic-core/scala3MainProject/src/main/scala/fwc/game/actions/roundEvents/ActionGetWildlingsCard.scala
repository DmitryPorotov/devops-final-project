package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, JsonParsableAction}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseGetWildlingsCard, SubPhaseSetWildlingsCard}
import ujson.Value

import scala.util.Random

case class ActionGetWildlingsCard(
                                   gameState: GameState,
                                   isRandom: Boolean
                                 ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseGetWildlingsCard]

    val newPhase =
      SubPhaseSetWildlingsCard(
        HouseType.getSeqOfAll,
        if isRandom
        then currentPhase.subPhaseWildlingsCard.copy(
          cardCode = gameState.boardCards.wildlings.head.code
        )
        else currentPhase.subPhaseWildlingsCard
      )

    gameState.copy(
      subPhase = newPhase,
      boardCards =
        if isRandom
        then gameState.boardCards.copy(
          wildlings = gameState.boardCards.wildlings.tail
        )
        else gameState.boardCards
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "getWildlingsCard",
    "isRandom" -> isRandom
  )

}

object ActionGetWildlingsCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionGetWildlingsCard =
    ActionGetWildlingsCard(
      gameState,
      json("isRandom").bool
    )
}
