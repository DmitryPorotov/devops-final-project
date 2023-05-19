package fwc.gameSaving.actions.planning

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.{RavenChoiceChangeOrder, RavenChoiceLookAtWildlingsCard, RavenChoiceNothing, RavenChoiceType}
import fwc.game.board.{TrackCourt, TrackThrone}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveRaidOrder
import fwc.game.phases.planningSubPhases
import fwc.game.phases.planningSubPhases.{SubPhaseRavenChangeOrder, SubPhaseRavenGetWildlingsCard}
import fwc.game.planningPhase.OrderRaid
import fwc.gameSaving.actions.action.NextOrderFinder
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionRavenChooseChangeOrderOrLookAtWildlingCard(
                                                             gameState: GameState,
                                                             houseType: HouseType,
                                                             ravenChoiceType: RavenChoiceType
                                                           )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[ActionRavenChooseChangeOrderOrLookAtWildlingCard]
    then throw new ActionException("Wrong phase")

    if gameState.tracks.ravenOwner != houseType
    then throw new ActionException(s"House $houseType is not 1st at the Court track")
    
    val newPhase = ravenChoiceType match
      case RavenChoiceChangeOrder => planningSubPhases.SubPhaseRavenChangeOrder(houseType)
      case RavenChoiceLookAtWildlingsCard => SubPhaseRavenGetWildlingsCard()
      case RavenChoiceNothing => 
        NextOrderFinder.nextSubPhase(gameState, OrderRaid)
    
    gameState.copy(
      subPhase = newPhase
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> ujson.Str("ravenChooseChangeOrderOrLookAtWildlingCard"),
    "houseType" -> houseType.toString,
    "ravenChoice" -> ravenChoiceType.toString
  )
}

object ActionRavenChooseChangeOrderOrLookAtWildlingCard extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionRavenChooseChangeOrderOrLookAtWildlingCard = {
    ActionRavenChooseChangeOrderOrLookAtWildlingCard(
      gameState,
      HouseType.fromString(json.obj("houseType").str),
      RavenChoiceType.fromString(json.obj("ravenChoice").str)
    )
  }
}
