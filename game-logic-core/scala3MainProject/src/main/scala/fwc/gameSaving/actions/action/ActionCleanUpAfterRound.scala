package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.actionPhase.DominanceTokensUsage
import fwc.game.board.{MilitaryUnit, TileNumber}
import fwc.game.eventsPhase.UsedMusteringPoints
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseCalculateGameWinner
import fwc.game.{GameState, gameRules}
import fwc.game.phases.roundEventsSubPhases.SubPhaseGetEventCards
import fwc.game.planningPhase.{AvailableOrders, PlacedOrders}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction}
import ujson.Value

case class ActionCleanUpAfterRound(
                                    gameState: GameState,
                                    isRandom: Boolean
                                  ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {

    val newPhase =
      if gameState.roundCounter == 10
      then SubPhaseCalculateGameWinner(HouseType.getSeqOfAll)
      else SubPhaseGetEventCards(HouseType.getSeqOfAll)

    val updatedArmies = gameState.armies.copy(
      gameState.armies.map(
        (tn: TileNumber, armies: Seq[MilitaryUnit]) =>
          tn -> armies.map(_.copy(isDefeated = false))
      )
    )

    gameState.copy(
      subPhase = newPhase,
      placedOrders = PlacedOrders(),
      availableOrders = AvailableOrders.initialize(),
      armies = updatedArmies,
      usedMusteringPoints = UsedMusteringPoints(),
      dominanceTokensUsage = DominanceTokensUsage(),
      roundCounter = gameState.roundCounter + 1,
      boardCards = gameState.boardCards.copy(
        roundEvents1 = gameState.boardCards.roundEvents1.tail :+ gameState.boardCards.roundEvents1.head,
        roundEvents2 = gameState.boardCards.roundEvents2.tail :+ gameState.boardCards.roundEvents2.head,
        roundEvents3 = gameState.boardCards.roundEvents3.tail :+ gameState.boardCards.roundEvents3.head,
      )
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "cleanUpAfterRound",
    "isRandom" -> isRandom
  )
}

object ActionCleanUpAfterRound extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionCleanUpAfterRound =
    ActionCleanUpAfterRound(
      gameState,
      json("isRandom").bool
    )

  def buildMessage(updatedGameState: GameState): ujson.Obj =
    ujson.Obj(
      "round" -> updatedGameState.roundCounter
    )
}
