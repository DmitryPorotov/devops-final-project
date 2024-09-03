package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.actions.{Action, JsonParsableAction}
import fwc.game.eventsPhase.Bids
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseOpenTrackBids, SubPhaseResolveTiesAfterBiddingOnTracks}
import ujson.Value

case class ActionOpenTrackBids(
                                gameState: GameState,
                                bids: Bids,
                              ) extends Action(gameState) with JsonSerializable:
  override def doAction(): GameState = {
    val doResolveTies =
      bids.foldLeft(Set())(
        (acc, htBid: (HouseType, Int)) =>
          acc + htBid._2
      ).size != 6

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseOpenTrackBids]
    
    val updatedTracks =
      if doResolveTies
      then gameState.tracks
      else gameState.tracks +
        (currentPhase.trackType -> bids.toSeq.view.sortWith((a, b) => b._2 < a._2).map(_._1).toSeq)


    val newPhase =
      if doResolveTies
      then SubPhaseResolveTiesAfterBiddingOnTracks(gameState.tracks.throneOwner, currentPhase.trackType)
      else EventCards.bidsFallThroughFromThrone(
        currentPhase.trackType,
        gameState.boardCards.roundEvents3.head,
        gameState.tracks.steelBladeOwner,
        gameState.wildlingCounter,
      )

    gameState.copy(
      subPhase = newPhase,
      tracks = updatedTracks,
      bids = Bids(),
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "openTrackBids",
    "bids" -> bids.toJson
  )

object ActionOpenTrackBids extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionOpenTrackBids =
    ActionOpenTrackBids(
      gameState,
      Bids.fromJson(json)
    )
}