package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.roundEventsSubPhases.*
import fwc.gameLoading.RoundEventCard
import fwc.gameSaving.actions.{Action, ActionException, ActionSetCard, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionSetEventCards(
                                gameState: GameState,
                                card1: RoundEventCard,
                                card2: RoundEventCard,
                                card3: RoundEventCard
                              ) 
  extends Action(gameState) with JsonSerializable with ActionSetCard {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseSetEventCards]
    then throw new ActionException("Wrong phase")

    val updatedBoardCards = gameState.boardCards.copy(
      roundEvents1 = gameState.boardCards.roundEvents1 prepended card1,
      roundEvents2 = gameState.boardCards.roundEvents2 prepended card2,
      roundEvents3 = gameState.boardCards.roundEvents3 prepended card3
    )

    val updatedWildlingsCounter = card1.wildlings + card2.wildlings + card3.wildlings + gameState.wildlingCounter

    val newPhase: SubPhase =
      if updatedWildlingsCounter >= 12
      then SubPhaseWildlingsBids(HouseType.getSeqOfAll, 6, true)
      else EventCards.fallThroughFromDeck1(gameState.tracks, updatedBoardCards)


    gameState.copy(
      subPhase = newPhase,
      boardCards = updatedBoardCards,
      wildlingCounter = if updatedWildlingsCounter > 12 then 12 else updatedWildlingsCounter
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "setEventCards",
    "card1" -> card1.code,
    "card2" -> card2.code,
    "card3" -> card3.code
  )
}

object ActionSetEventCards extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionSetEventCards =
    ActionSetEventCards(
      gameState,
      gameRules.boardCards.roundEvents1.find(_.code == json("card1").num.toInt).head,
      gameRules.boardCards.roundEvents2.find(_.code == json("card2").num.toInt).head,
      gameRules.boardCards.roundEvents3.find(_.code == json("card3").num.toInt).head
    )
}
