package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction}
import fwc.game.board.TrackType
import fwc.game.eventsPhase.Supplies
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseDisbandUnit, SubPhaseRecalculateSupplies}
import ujson.Value


case class ActionRecalculateSupplies(
                                      gameState: GameState
                                    ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
//    if !gameState.subPhase.isInstanceOf[SubPhaseRecalculateSupplies]
//    then throw new ActionException("Wrong phase")

    val updatedSupplies = Supplies.recalculateSupplyTrack(gameState.armies)

    val armiesToConsolidate = Supplies.findArmiesToConsolidate(gameState.armies, updatedSupplies)
      .filter(_._2.nonEmpty)

    val newPhase =
      if armiesToConsolidate.nonEmpty
      then
        SubPhaseDisbandUnit(
          Supplies.getHouseToConsolidate(armiesToConsolidate, gameState.tracks(TrackType.Throne)),
          UnitDisbandNextStepDeck2
        )
      else
        EventCards.fallThroughFromDeck2(
          gameState.tracks,
          gameState.boardCards,
          gameState.wildlingCounter,
        )

    gameState.copy(
      subPhase = newPhase,
      supplies = updatedSupplies
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "recalculateSupplies"
  )
}

object ActionRecalculateSupplies extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionRecalculateSupplies =
    ActionRecalculateSupplies(gameState)
}
