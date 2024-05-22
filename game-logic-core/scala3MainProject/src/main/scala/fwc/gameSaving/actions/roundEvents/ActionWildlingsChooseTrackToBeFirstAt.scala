package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseMuster
import fwc.gameSaving.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionWildlingsChooseTrackToBeFirstAt(
                                                  gameState: GameState,
                                                  houseType: HouseType,
                                                  trackType: TrackType
                                                )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseMuster]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseMuster].houseType != houseType
    then throw new ActionException("Wrong house")

    gameState.copy(
      subPhase = WildlingsCards.getNextNonWildlingsPhase(
        gameState.wildlingsStartedFrom12Points.head,
        gameState.tracks,
        gameState.boardCards
      ),
      tracks = gameState.tracks.setHouseHighestOnTrack(houseType, trackType),
      wildlingsStartedFrom12Points = None
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsChooseTrackToBeFirstAt",
    "houseType" -> houseType.toString,
    "track" -> trackType.toString
  )
}

object ActionWildlingsChooseTrackToBeFirstAt extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsChooseTrackToBeFirstAt =
    ActionWildlingsChooseTrackToBeFirstAt(
      gameState,
      HouseType.fromString(json("houseType").str),
      TrackType.fromString(json("track").str)
    )
}