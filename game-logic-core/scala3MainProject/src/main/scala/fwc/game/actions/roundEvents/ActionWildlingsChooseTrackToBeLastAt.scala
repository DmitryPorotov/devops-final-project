package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.board.TrackType
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseMuster, SubPhaseWildlingsChooseTrackToBeLastAt}
import ujson.Value

class ActionWildlingsChooseTrackToBeLastAt(
                                            gameState: GameState,
                                            houseType: HouseType,
                                            trackType: TrackType
                                          )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsChooseTrackToBeLastAt]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseWildlingsChooseTrackToBeLastAt]

    if currentPhase.houseTypes.head != houseType
    then throw new ActionException("Wrong house")

    if trackType == TrackType.Throne
    then throw new ActionException("You should choose either the Fiefdoms or King's Court Influence track")

    val newPhase =
      if currentPhase.houseTypes.size == 1
      then WildlingsCards.getNextNonWildlingsPhase(
        gameState.wildlingsStartedFrom12Points.head,
        gameState.tracks,
        gameState.boardCards,
        gameState.wildlingCounter,
      )
      else currentPhase.copy(
        currentPhase.houseTypes.tail
      )

    gameState.copy(
      subPhase = newPhase,
      tracks = gameState.tracks.setHouseLowestOnTrack(houseType, trackType),
      wildlingsStartedFrom12Points =
        if currentPhase.houseTypes.size == 1
        then None
        else gameState.wildlingsStartedFrom12Points
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsChooseTrackToBeLastAt",
    "houseType" -> houseType.toString,
    "track" -> trackType.toString
  )
}

object ActionWildlingsChooseTrackToBeLastAt extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsChooseTrackToBeLastAt =
    ActionWildlingsChooseTrackToBeLastAt(
      gameState,
      HouseType.fromString(json("houseType").str),
      TrackType.fromString(json("track").str)
    )
}
