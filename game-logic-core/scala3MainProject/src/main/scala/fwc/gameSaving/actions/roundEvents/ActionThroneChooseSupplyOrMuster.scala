package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.eventsPhase.cards.*
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseChooseUpdateSupplyOrMuster, SubPhaseMuster, SubPhaseRecalculateSupplies}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionThroneChooseSupplyOrMuster(
                                             gameState: GameState,
                                             houseType: HouseType,
                                             choice: EventCardChoiceType
                                           ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseChooseUpdateSupplyOrMuster]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseChooseUpdateSupplyOrMuster].houseType != houseType
    then throw new ActionException("Wrong house")

    val newPhase =
      if choice == EventCardChoiceA
      then SubPhaseRecalculateSupplies()
      else if choice == EventCardChoiceB
        then SubPhaseMuster(gameState.tracks.throneOwner)
        else EventCards.fallThroughFromDeck2(gameState.tracks, gameState.boardCards)

    gameState.copy(newPhase)
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "throneChooseSupplyOrMuster",
    "houseType" -> houseType.toString,
    "choice" -> choice.toString
  )
}

object ActionThroneChooseSupplyOrMuster extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionThroneChooseSupplyOrMuster =
    ActionThroneChooseSupplyOrMuster(
      gameState,
      HouseType.fromString(json("houseType").str),
      EventCardChoiceType.fromString(json("choice").str)
    )
}