package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.actions.{Action, ActionException, JsonParsableAction}
import fwc.game.phases.actionSubPhases.SubPhaseRefreshTidesOfBattleDeck
import fwc.game.{GameState, gameRules}
import fwc.gameLoading.TidesOfBattleCard

case class ActionRefreshTidesOfBattleDeck(
                                           gameState: GameState,
                                           newCards: Seq[TidesOfBattleCard]
                                         ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState =
    if !gameState.subPhase.isInstanceOf[SubPhaseRefreshTidesOfBattleDeck]
    then throw new ActionException("Wrong phase")

    gameState.copy(
      boardCards = gameState.boardCards.copy(tidesOfBattle = newCards)
    )

  override def toJson: ujson.Value = ujson.Obj(
    Action.actionTypeJsonKey -> "refreshTidesOfBattleDeck",
    "newCards" -> ujson.Value(
       newCards.map(c => ujson.Num(c.code))
    ),
  )
}

object ActionRefreshTidesOfBattleDeck extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: ujson.Value): ActionRefreshTidesOfBattleDeck =
    ActionRefreshTidesOfBattleDeck(
      gameState,
      json("newCards").arr.map(c => gameRules.boardCards.tidesOfBattle.find(_.code == c.num.toInt).head).toSeq
    )
}
