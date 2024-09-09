package fwc.gameSaving

import fwc.{GameSettings, JsonParsable, JsonSerializable}
import fwc.game.GameState
import fwc.game.actions.Action
import fwc.game.eventsPhase.cards.BoardCards
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
  def toFullJson: Value =
    toJson.asInstanceOf[ujson.Obj].value.addOne("currentGameState" -> currentGameState.toJson)
}

object GameReplay extends JsonParsable {
  override def fromJson(json: Value): GameReplay =
    val (settings, startingCards, startingGameState) = initStartingState(json)
    val (actions, currentGameState) = json("actions").arr.foldRight((Seq[Action](),startingGameState))(
      (cur, acc) =>
        val (actions, curState) = acc
        val curAction = Action.fromJson(curState, cur)
        val newState = curAction.doAction()
        (actions prepended curAction, newState)
    )
    GameReplay(
      settings,
      startingCards,
      currentGameState,
      actions
    )

  private def initStartingState(json: Value): (GameSettings, BoardCards, GameState) = {
    val settings = GameSettings.fromJson(json("gameSettings"))
    val startingCards = BoardCards.fromJson(json("startingBoardCards"))
    val startingGameState = fwc.game.initializeGameState(false).copy(
      boardCards = startingCards
    )
    (settings, startingCards, startingGameState)
  }

  def fromJsonDebug(json: Value, onDoAction: Option[((currentState: GameState, action: Action, newState: GameState) => Unit)] = None): GameReplay = {
    val (settings, startingCards, startingGameState) = initStartingState(json)
    val (actions, currentGameState) = json("actions").arr.foldRight((Seq[Action](), startingGameState))(
      (cur, acc) =>
        val (actions, curState) = acc
        val curAction = Action.fromJson(curState, cur)
        val newState = curAction.doAction()
        if onDoAction.nonEmpty
        then onDoAction.head(curState, curAction, newState)
        (actions prepended curAction, newState)
    )
    GameReplay(
      settings,
      startingCards,
      currentGameState,
      actions
    )
  }
}
