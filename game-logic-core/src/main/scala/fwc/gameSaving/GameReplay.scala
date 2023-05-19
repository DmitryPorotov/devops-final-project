package fwc.gameSaving

import fwc.{GameSettings, JsonParsable, JsonSerializable}
import fwc.game.GameState
import fwc.game.eventsPhase.cards.BoardCards
import fwc.gameSaving.actions.Action
import ujson.Value

case class GameReplay(
                       gameSettings: GameSettings,
                       startingBoardCards: BoardCards,
                       currentGameState: GameState,
                       actions: Seq[Action]
                     ) extends JsonSerializable {
  override def toJson: Value = ujson.Obj(
    "gameSettings" -> gameSettings.toJson,
    "startingBoardCards" -> startingBoardCards.toJson,
    "actions" -> ujson.Arr.from(
      actions.map(_.toJson)
    )
  )
}

object GameReplay extends JsonParsable {
  override def fromJson(json: Value): GameReplay =
    val settings = GameSettings.fromJson(json("gameSettings"))
    val startingCards = BoardCards.fromJson(json("startingBoardCards"))
    val startingGameState = fwc.game.initializeGameState(false).copy(
      boardCards = startingCards
    )
    val (actions, currentGameState) = json("actions").arr.foldRight((Seq[Action](),startingGameState))(
      (cur, acc) =>
        val curAction = Action.fromJson(acc._2, cur)
        val newState = curAction.doAction()
        (acc._1 prepended curAction, newState)
    )
    GameReplay(
      settings,
      startingCards,
      currentGameState,
      actions
    )
}
