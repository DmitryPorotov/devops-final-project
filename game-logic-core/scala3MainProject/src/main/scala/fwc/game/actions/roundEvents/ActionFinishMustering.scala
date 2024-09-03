package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.*
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.roundEventsSubPhases.SubPhaseMuster
import ujson.Value

case class ActionFinishMustering(
                                  gameState: GameState,
                                  houseType: HouseType,
                                )
  extends Action(gameState)
    with PlayerAction(houseType)
    with JsonSerializable {


  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseMuster]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseMuster].houseType != houseType
    then throw new ActionException("Wrong house")
    
    val newPhase = {
      val nextHouse = gameState.tracks.getNextHouseOnThrone(houseType)
      if nextHouse == gameState.tracks.throneOwner
      then EventCards.fallThroughFromDeck2(gameState.tracks, gameState.boardCards, gameState.wildlingCounter)
      else SubPhaseMuster(nextHouse)
    }
    gameState.copy(
      subPhase = newPhase
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "finishMustering",
    "houseType" -> houseType.toString,
  )
}

object ActionFinishMustering extends JsonParsableAction {

  override def fromJson(gameState: GameState, json: Value): ActionFinishMustering = {
    ActionFinishMustering(
      gameState,
      HouseType.fromString(json("houseType").str)
    )
  }
}
