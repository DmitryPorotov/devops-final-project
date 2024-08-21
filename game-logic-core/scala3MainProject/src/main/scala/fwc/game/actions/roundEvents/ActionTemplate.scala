package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseMuster
import ujson.Value

case class ActionTemplate(
                           gameState: GameState,
                           houseType: HouseType,
                         ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseMuster]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseMuster].houseType != houseType
    then throw new ActionException("Wrong house")

    ???
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "template",
    "houseType" -> houseType.toString
  )
}

object ActionTemplate extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): Action =
    ActionTemplate(
      gameState,
      HouseType.fromString(json("houseType").str)
    )
}
