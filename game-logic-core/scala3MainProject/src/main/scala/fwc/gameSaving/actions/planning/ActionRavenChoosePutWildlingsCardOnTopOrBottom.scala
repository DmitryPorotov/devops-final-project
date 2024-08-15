package fwc.gameSaving.actions.planning

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actionPhase.DominanceTokensUsage
import fwc.game.board.DominanceTokenMessengerRaven
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom
import fwc.game.planningPhase.OrderType
import fwc.gameSaving.actions.action.NextOrderFinder
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionRavenChoosePutWildlingsCardOnTopOrBottom(
                                                           gameState: GameState,
                                                           houseType: HouseType,
                                                           isPutOnTop: Boolean
                                                         )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom]
    then throw new ActionException("Wrong phase")

    if gameState.tracks.ravenOwner != houseType
    then throw new ActionException(s"House $houseType does has not Messenger Raven token")
    
    if isPutOnTop 
    then gameState
    else {
      val newWildlingsCards = gameState.boardCards.wildlings.tail appended gameState.boardCards.wildlings.head
      
      gameState.copy(
        subPhase = NextOrderFinder.nextSubPhase(gameState, OrderType.OrderRaid),
        boardCards = gameState.boardCards.copy(
          wildlings = newWildlingsCards
        ),
        dominanceTokensUsage = DominanceTokensUsage(
          gameState.dominanceTokensUsage.usage + (DominanceTokenMessengerRaven -> true)
        )
      )
    }
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "ravenChoosePutWildlingsCardOnTopOrBottom",
    "houseType" -> ujson.Str(houseType.toString),
    "isPutOnTop" -> ujson.Bool(isPutOnTop)
  )
  
}

object ActionRavenChoosePutWildlingsCardOnTopOrBottom extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionRavenChoosePutWildlingsCardOnTopOrBottom = {
    ActionRavenChoosePutWildlingsCardOnTopOrBottom(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("isPutOnTop").bool
    )
  }
}
