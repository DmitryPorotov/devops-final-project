package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.eventsPhase.Bids
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseResolveTiesAfterBiddingOnWildlings, SubPhaseWildlingsCard}
import ujson.Value

case class ActionResolveTiesAfterBiddingOnWildlings(
                                                     gameState: GameState,
                                                     houseType: HouseType,
                                                     winnerLoser: HouseType
                                                   )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseResolveTiesAfterBiddingOnWildlings]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseResolveTiesAfterBiddingOnWildlings]

    if gameState.tracks.throneOwner != houseType
    then throw new ActionException("Wrong house")

    if !currentPhase.houseTypes.contains(winnerLoser)
    then throw new ActionException(s"${if currentPhase.isWinner then "Winner" else "Loser"} should " +
      s"be chosen from one of ${currentPhase.houseTypes.mkString(", ")}")

    gameState.copy(
      subPhase = SubPhaseWildlingsCard(
        gameState.bids.toSeq.map(_._1),
        winnerLoser,
        gameState.boardCards.wildlings.head.code,
        currentPhase.isWinner
      )
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveTiesAfterBiddingOnWildlings",
    "houseType" -> houseType.toString,
    "winnerLoser" -> winnerLoser.toString
  )
}

object ActionResolveTiesAfterBiddingOnWildlings extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveTiesAfterBiddingOnWildlings =
    ActionResolveTiesAfterBiddingOnWildlings(
      gameState,
      HouseType.fromString(json("houseType").str),
      HouseType.fromString(json("winnerLoser").str)
    )
}