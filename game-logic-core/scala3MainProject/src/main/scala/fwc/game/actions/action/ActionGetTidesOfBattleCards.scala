package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, JsonParsableAction}
import fwc.game.actions.roundEvents.ActionGetEventCards
import fwc.game.eventsPhase.cards.TidesOfBattleDeckEmptyException
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseGetTidesOfBattleCards, SubPhaseRefreshTidesOfBattleDeck, SubPhaseSetTidesOfBattleCards}
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
          subPhase = {
            if gameState.subPhase.isInstanceOf[SubPhaseGetTidesOfBattleCards]
            then SubPhaseSetTidesOfBattleCards(
              HouseType.getSeqOfAll,
              Some(card1.code)
            )
            else {
              val phase = gameState.subPhase.asInstanceOf[SubPhaseSetTidesOfBattleCards]
              if phase.defenderCard.isEmpty
              then phase.copy(defenderCard = Some(card1.code))
              else phase.copy(attackerCard = Some(card1.code))
            }
          }
          ,
          boardCards = boardCards1
        )
      }
      catch
        case _: TidesOfBattleDeckEmptyException =>
          gameState.copy(
            subPhase = SubPhaseRefreshTidesOfBattleDeck()
          )
    else gameState.copy(
      subPhase = SubPhaseSetTidesOfBattleCards(HouseType.getSeqOfAll),
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
