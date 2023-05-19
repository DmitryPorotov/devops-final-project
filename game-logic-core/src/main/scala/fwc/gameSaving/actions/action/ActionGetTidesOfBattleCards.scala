package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.eventsPhase.cards.TidesOfBattleDeckEmptyException
import fwc.game.phases.actionSubPhases.{SubPhaseGetTidesOfBattleCards, SubPhaseRefreshTidesOfBattleDeck, SubPhaseSetTidesOfBattleCards}
import fwc.gameSaving.actions.{Action, JsonParsableAction}
import fwc.gameSaving.actions.roundEvents.ActionGetEventCards
import ujson.Value

import scala.util.Random

case class ActionGetTidesOfBattleCards(
                                        gameState: GameState,
                                        isRandom: Boolean
                                      ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
    if isRandom
    then
      try {
        val (card1, boardCards1) = gameState.boardCards.dequeueTidesOfBattleCard()
        gameState.copy(
          subPhase =
            if gameState.subPhase.isInstanceOf[SubPhaseGetTidesOfBattleCards]
            then SubPhaseSetTidesOfBattleCards(
              Some(card1.code)
            )
            else gameState.subPhase.asInstanceOf[SubPhaseSetTidesOfBattleCards].copy(
              defenderCard = Some(card1.code)
            )
          ,
          boardCards = boardCards1
        )
      }
      catch
        case _: TidesOfBattleDeckEmptyException =>
          return gameState.copy(
            subPhase = SubPhaseRefreshTidesOfBattleDeck()
          )
    else gameState.copy(
      subPhase = SubPhaseSetTidesOfBattleCards(),
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "getTidesOfBattleCards",
    "isRandom" -> isRandom
  )

}

object ActionGetTidesOfBattleCards extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionGetTidesOfBattleCards =
    ActionGetTidesOfBattleCards(
      gameState,
      json("isRandom").bool
    )
}
