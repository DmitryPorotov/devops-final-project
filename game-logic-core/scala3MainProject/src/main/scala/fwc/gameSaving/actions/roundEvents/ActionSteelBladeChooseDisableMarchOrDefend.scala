package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.eventsPhase.cards.*
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.phases.roundEventsSubPhases.{SubPhaseChooseDisableMarchPlus1OrDefendOrders, SubPhaseDisableOrder}
import fwc.game.planningPhase.OrderType
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionSteelBladeChooseDisableMarchOrDefend(
                                                       gameState: GameState,
                                                       houseType: HouseType,
                                                       choice: EventCardChoiceType
                                                     )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseChooseDisableMarchPlus1OrDefendOrders]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseChooseDisableMarchPlus1OrDefendOrders].houseType != houseType
    then throw new ActionException("Wrong house")

    val newPhase =
      if choice == EventCardChoiceType.ChoiceA
      then SubPhaseDisableOrder(HouseType.getSeqOfAll, OrderType.Defend)
      else if choice == EventCardChoiceType.ChoiceB
        then SubPhaseDisableOrder(HouseType.getSeqOfAll, OrderType.March)
        else SubPhaseAddOrder(HouseType.getSeqOfAll)

    gameState.copy(newPhase)
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "steelBladeChooseDisableMarchOrDefend",
    "houseType" -> houseType.toString,
    "choice" -> choice.toString
  )
}

object ActionSteelBladeChooseDisableMarchOrDefend extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionSteelBladeChooseDisableMarchOrDefend =
    ActionSteelBladeChooseDisableMarchOrDefend(
      gameState,
      HouseType.fromString(json("houseType").str),
      EventCardChoiceType.fromString(json("choice").str)
    )
}