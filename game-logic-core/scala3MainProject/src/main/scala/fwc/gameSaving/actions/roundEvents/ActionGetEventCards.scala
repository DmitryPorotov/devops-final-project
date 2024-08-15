package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.{GameState, gameRules}
import fwc.game.phases.roundEventsSubPhases.{SubPhaseGetEventCards, SubPhaseSetEventCards}
import fwc.gameSaving.actions.{Action, JsonParsableAction}
import ujson.Value

import scala.util.Random

case class ActionGetEventCards(
                                gameState: GameState,
                                isRandom: Boolean
                              ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
    gameState.subPhase match {
      case _: SubPhaseGetEventCards =>
        val newPhase =
          if isRandom
          then SubPhaseSetEventCards(
            HouseType.getSeqOfAll,
              Some(gameState.boardCards.roundEvents1.head),
              Some(gameState.boardCards.roundEvents2.head),
              Some(gameState.boardCards.roundEvents3.head),
            )
          else SubPhaseSetEventCards(HouseType.getSeqOfAll)

        val updatedBoardCards =
          if isRandom
          then gameState.boardCards.copy(
            gameState.boardCards.roundEvents1.tail,
            gameState.boardCards.roundEvents2.tail,
            gameState.boardCards.roundEvents3.tail,
          )
          else gameState.boardCards

        gameState.copy(
          subPhase = newPhase,
          boardCards = updatedBoardCards
        )

      case set: SubPhaseSetEventCards =>
        val updatedBoardCards =
          if set.card1.head.code == 3 && set.card2.head.code == 3
          then gameState.boardCards.copy(
            roundEvents1 =
              Random.shuffle(
                gameState.boardCards.roundEvents1
                  prepended set.card1.head
              ),
            roundEvents2 = Random.shuffle(
              gameState.boardCards.roundEvents2
                prepended set.card2.head
            )
          )
          else if set.card1.head.code == 3
          then gameState.boardCards.copy(
              roundEvents1 = Random.shuffle(
                gameState.boardCards.roundEvents1
                  prepended set.card1.head
              )
            )
          else if set.card2.head.code == 3
          then gameState.boardCards.copy(
              roundEvents2 = Random.shuffle(
                gameState.boardCards.roundEvents2
                  prepended set.card2.head
              )
            )
          else gameState.boardCards
        gameState.copy(
          subPhase = set.copy(
            HouseType.getSeqOfAll,
            Some(updatedBoardCards.roundEvents1.head),
            Some(updatedBoardCards.roundEvents2.head),
          ),
          boardCards = updatedBoardCards.copy(
            updatedBoardCards.roundEvents1.tail,
            updatedBoardCards.roundEvents2.tail,
          )
        )
    }
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "getEventCards",
    "isRandom" -> isRandom
  )

}

object ActionGetEventCards extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionGetEventCards =
    ActionGetEventCards(
      gameState,
      json("isRandom").bool
    )
}
