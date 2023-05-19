package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.board.TrackThrone
import fwc.game.eventsPhase.cards.*
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseChooseTracksBidsOrCollectTaxes, SubPhaseCollectTaxes, SubPhaseTracksBids}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}

case class ActionRavenChooseTrackBidsOrCollectTaxes(
                                              gameState: GameState,
                                              houseType: HouseType,
                                              choice: EventCardChoiceType
                                            ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {

  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseChooseTracksBidsOrCollectTaxes]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseChooseTracksBidsOrCollectTaxes].houseType != houseType
    then throw new ActionException("Wrong house")

    val newPhase =
      if choice == EventCardChoiceA
      then SubPhaseTracksBids(HouseType.getSeqOfAll, TrackThrone)
      else if choice == EventCardChoiceB
      then SubPhaseCollectTaxes()
      else EventCards.getPhaseForDeck3Card(gameState.boardCards.roundEvents3.head, gameState.tracks.steelBladeOwner)

    gameState.copy(newPhase)
  }

  override def toJson: ujson.Value = ujson.Obj(
    Action.actionTypeJsonKey -> "ravenChooseTrackBidsOrCollectTaxes",
    "houseType" -> houseType.toString,
    "choice" -> choice.toString
  )
}

object ActionRavenChooseTrackBidsOrCollectTaxes extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: ujson.Value): ActionRavenChooseTrackBidsOrCollectTaxes =
    ActionRavenChooseTrackBidsOrCollectTaxes(
      gameState,
      HouseType.fromString(json("houseType").str),
      EventCardChoiceType.fromString(json("choice").str)
    )
}