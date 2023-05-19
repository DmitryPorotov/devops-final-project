package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.board.{TrackCourt, TrackFiefdoms, TrackThrone}
import fwc.game.eventsPhase.Bids
import fwc.game.{GameState, gameRules}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseResolveTiesAfterBiddingOnTracks, SubPhaseTracksBids}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionTrackBids(
                            gameState: GameState,
                            houseType: HouseType,
                            bid: Int
                          ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseTracksBids]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseTracksBids]

    if !currentPhase.houseTypes.contains(houseType)
    then throw new ActionException("Wrong house")

    val updatedPowerTokens =
      if gameState.powerTokens(houseType) < bid
      then throw new ActionException(s"You have ${gameState.powerTokens(houseType)} power tokens" +
        s" which is not enough to place this bid ($bid)")
      else gameState.powerTokens + (houseType -> (gameState.powerTokens(houseType) - bid))

    val updatedBids = gameState.bids + (houseType -> bid)

    val isBiddingFinished = updatedBids.size >= 6

    val doResolveTies =
      if isBiddingFinished
      then updatedBids.foldLeft(Set())(
        (acc, htBid: (HouseType, Int)) =>
          acc + htBid._2
      ).size != 6
      else false

    val updatedTracks =
      if doResolveTies
      then gameState.tracks
      else gameState.tracks +
        (currentPhase.trackType -> updatedBids.toSeq.view.sortWith((a, b) => b._2 < a._2).map(_._1).toSeq)


    val newPhase =
      if !isBiddingFinished
      then SubPhaseTracksBids(currentPhase.houseTypes.filter(_ != houseType), currentPhase.trackType)
      else
        if doResolveTies
        then SubPhaseResolveTiesAfterBiddingOnTracks(gameState.tracks.throneOwner, currentPhase.trackType)
        else EventCards.bidsFallThroughFromThrone(
          currentPhase.trackType,
          gameState.boardCards.roundEvents3.head,
          gameState.tracks.steelBladeOwner
        )

    gameState.copy(
      subPhase = newPhase,
      tracks = updatedTracks,
      bids = if isBiddingFinished && !doResolveTies then Bids() else updatedBids,
      powerTokens = updatedPowerTokens
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "trackBids",
    "houseType" -> houseType.toString,
    "bid" -> bid
  )
}


object ActionTrackBids extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionTrackBids =
    ActionTrackBids(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("bid").num.toInt
    )
}