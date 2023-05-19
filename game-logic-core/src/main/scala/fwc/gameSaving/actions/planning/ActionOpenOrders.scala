package fwc.gameSaving.actions.planning

import fwc.JsonSerializable
import fwc.game.gameRules
import fwc.game.{FWCException, GameState}
import fwc.game.board.{TrackCourt, TrackThrone, Tracks}
import fwc.game.houses.HouseType
import fwc.game.phases.{SubPhase, planningSubPhases}
import fwc.game.phases.actionSubPhases.{SubPhaseResolveMarchOrder, SubPhaseResolveRaidOrder, SubPhaseResolveSpecialConsolidatePower}
import fwc.game.phases.planningSubPhases.{SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard, SubPhaseReadyToOpenOrders}
import fwc.game.planningPhase.{OrderConsolidatePower, OrderMarch, OrderRaid, PlacedOrders}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionOpenOrders(
                             gameState: GameState,
                             houseType: HouseType
                           ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseReadyToOpenOrders]
    then throw new ActionException("Wrong phase")
    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseReadyToOpenOrders]
    val housesLeftToBeReady = currentPhase.houseTypes.filter(_ != houseType)
    val newPhase = if housesLeftToBeReady.nonEmpty
      then SubPhaseReadyToOpenOrders(housesLeftToBeReady)
    else planningSubPhases.SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard(
      gameState.tracks.ravenOwner
    )
    gameState.copy(
      subPhase = newPhase
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> ujson.Str("openOrders"),
    "houseType" -> ujson.Str(houseType.toString),
  )
}

object ActionOpenOrders extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionOpenOrders = {
    ActionOpenOrders(
      gameState,
      HouseType.fromString(json("houseType").str)
    )
  }
}
