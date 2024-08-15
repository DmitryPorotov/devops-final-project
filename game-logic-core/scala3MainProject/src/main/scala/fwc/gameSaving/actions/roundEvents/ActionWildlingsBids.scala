package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.eventsPhase.Bids
import fwc.game.{GameState, gameRules}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseGetWildlingsCard, SubPhaseResolveTiesAfterBiddingOnWildlings, SubPhaseWildlingsBids, SubPhaseWildlingsCard}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionWildlingsBids(
                                gameState: GameState,
                                houseType: HouseType,
                                bid: Int
                              ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsBids]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseWildlingsBids]

    if !currentPhase.houseTypes.contains(houseType)
    then throw new ActionException("Wrong house")

    val updatedPowerTokens =
      if gameState.powerTokens(houseType) < bid
      then throw new ActionException(s"You have ${gameState.powerTokens(houseType)} power tokens" +
        s" which is not enough to place this bid ($bid)")
      else gameState.powerTokens + (houseType -> (gameState.powerTokens(houseType) - bid))

    val updatedBids = gameState.bids + (houseType -> bid)

    val isBiddingFinished = updatedBids.size >= currentPhase.numberOfParticipants

    if !isBiddingFinished
    then return gameState.copy(
      subPhase = currentPhase.copy(
        currentPhase.houseTypes.filter(_ != houseType)
      ),
      powerTokens = updatedPowerTokens,
      bids = updatedBids,
      wildlingsStartedFrom12Points = Some(currentPhase.wildlingsStartedFrom12Points)
    )

    val isWinAndWinnerLoser = gameState.bids.getLoserOrWinnerCandidatesInWildlingsBids(gameState.wildlingCounter)

    val newPhase =
      if isWinAndWinnerLoser._2.size == 1
      then 
        SubPhaseGetWildlingsCard(
          HouseType.getSeqOfAll,
          SubPhaseWildlingsCard(
            gameState.bids.toSeq.map(_._1),
            isWinAndWinnerLoser._2.head,
            -1,
            isWinAndWinnerLoser._1
          )
        )
      else SubPhaseResolveTiesAfterBiddingOnWildlings(isWinAndWinnerLoser._2, isWinAndWinnerLoser._1)


    gameState.copy(
      subPhase = newPhase,
      powerTokens = updatedPowerTokens,
      bids = updatedBids,
      wildlingsStartedFrom12Points = Some(currentPhase.wildlingsStartedFrom12Points),
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsBids",
    "houseType" -> houseType.toString,
    "bid" -> bid
  )
}

object ActionWildlingsBids extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsBids =
    ActionWildlingsBids(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("bid").num.toInt
    )
}