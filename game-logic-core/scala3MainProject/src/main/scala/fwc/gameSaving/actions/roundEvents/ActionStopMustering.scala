package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseMuster
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionStopMustering(
                                gameState: GameState,
                                houseType: HouseType,
                              ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseMuster]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseMuster].houseType != houseType
    then throw new ActionException("Wrong house")

    val newPhase =
      val idx: Int = gameState.tracks(TrackType.Throne).indexOf(houseType)
      if idx >= 5
      then EventCards.fallThroughFromDeck2(gameState.tracks, gameState.boardCards)
      else SubPhaseMuster(gameState.tracks(TrackType.Throne)(idx + 1))

    gameState.copy(newPhase)
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "stopMustering",
    "houseType" -> houseType.toString
  )
}

object ActionStopMustering extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionStopMustering =
    ActionStopMustering(
      gameState,
      HouseType.fromString(json("houseType").str)
    )
}
