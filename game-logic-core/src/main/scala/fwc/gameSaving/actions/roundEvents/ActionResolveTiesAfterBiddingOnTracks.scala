package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.eventsPhase.Bids
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseResolveTiesAfterBiddingOnTracks
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionResolveTiesAfterBiddingOnTracks(
                                                  gameState: GameState,
                                                  houseType: HouseType,
                                                  resolution: Seq[HouseType]
                                                )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseResolveTiesAfterBiddingOnTracks]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseResolveTiesAfterBiddingOnTracks]

    if currentPhase.houseType != houseType
    then throw new ActionException("Wrong house")

    if resolution.size != 6
    then throw new ActionException("Resolution should contain all houses")

    if !gameState.bids.validateTieResolution(resolution)
    then throw new ActionException("Resolution is invalid according to bids placed")

    val updatedTracks =
      gameState.tracks + (currentPhase.trackType -> resolution)

    val newPhase =
      EventCards.bidsFallThroughFromThrone(
        currentPhase.trackType,
        gameState.boardCards.roundEvents3.head,
        updatedTracks.steelBladeOwner
      )

    gameState.copy(
      subPhase = newPhase,
      tracks = updatedTracks,
      bids = Bids()
    )
  }

  override def toJson: ujson.Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveTiesAfterBiddingOnTracks",
    "houseType" -> houseType.toString,
    "resolution" -> ujson.Arr.from(resolution.map(_.toString))
  )
}

object ActionResolveTiesAfterBiddingOnTracks extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveTiesAfterBiddingOnTracks =
    ActionResolveTiesAfterBiddingOnTracks(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("resolution").arr.map(ht => HouseType.fromString(ht.str)).toSeq
    )
}
