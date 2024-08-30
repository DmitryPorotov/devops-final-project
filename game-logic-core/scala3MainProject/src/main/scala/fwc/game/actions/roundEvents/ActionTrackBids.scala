package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.eventsPhase.Bids
import fwc.game.{GameState, gameRules}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseOpenTrackBids, SubPhaseResolveTiesAfterBiddingOnTracks, SubPhaseTracksBids}
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

    val newPhase =
      if !isBiddingFinished
      then SubPhaseTracksBids(currentPhase.houseTypes.filter(_ != houseType), currentPhase.trackType)
      else SubPhaseOpenTrackBids(currentPhase.trackType)

    gameState.copy(
      subPhase = newPhase,
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