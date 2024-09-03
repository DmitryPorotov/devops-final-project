package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.board.{MilitaryUnit, TileNumber, TrackType}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack
import ujson.Value

import scala.collection.mutable
import scala.util.Try

class ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack(
                                                          gameState: GameState,
                                                          houseType: HouseType,
                                                          track: Option[TrackType],
                                                          units: Option[Map[TileNumber, MilitaryUnit]]
                                                        )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack].houseType != houseType
    then throw new ActionException("Wrong house")

    if (track.isEmpty && units.isEmpty) || (track.nonEmpty && units.nonEmpty)
    then throw new ActionException("Choose one of; A. 2 units to kill or B. be reduced 2 positions of the highest track")

    val gameState2 = gameState.copy(
      WildlingsCards.getNextNonWildlingsPhase(
        gameState.wildlingsStartedFrom12Points.head,
        gameState.tracks,
        gameState.boardCards,
        gameState.wildlingCounter,
      ),
      wildlingsStartedFrom12Points = None
    )

    if track.nonEmpty
    then
      val highestTracks = gameState2.tracks.getHighestTracks(houseType)
      if highestTracks.size == 1
      then gameState2.copy(
        tracks = gameState2.tracks.reduce2PositionsOnTrack(highestTracks.head, houseType)
      )
      else if highestTracks.contains(track)
        then gameState2.copy(
          tracks = gameState2.tracks.reduce2PositionsOnTrack(track.head, houseType)
        )
        else throw new ActionException(s"Track ${track.head} is not your highest track")
    else
      if units.head.size < 2
      then throw new ActionException(s"You need to disband 2 units")

      gameState2.copy(
        armies = gameState2.armies.disbandMilitaryUnit(units.head.head._1, units.head.head._2)
          .disbandMilitaryUnit(units.head.tail.head._1, units.head.tail.head._2)
      )
  }

  override def toJson: Value =
    val json = ujson.Obj(
      Action.actionTypeJsonKey -> "wildlingsChooseKill2UnitsOr2PositionsOnTrack",
      "houseType" -> houseType.toString
    )
    if track.nonEmpty
    then json.obj.addOne("track" -> track.head.toString)
    if units.nonEmpty
    then json.obj.addOne("units" -> mutable.LinkedHashMap.from(
      units.head.map((tn: TileNumber, mu: MilitaryUnit) => tn.toString -> mu.toJson)
    ))
    json
}

object ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack =
    ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack(
      gameState,
      HouseType.fromString(json("houseType").str),
      Try(json("track").strOpt.map(str => TrackType.fromString(str))).getOrElse(None),
      Try[Option[Map[TileNumber, MilitaryUnit]]](json("units").objOpt.map(
        _.map(
          (tn: String, mu: Value) =>
          tn.toInt -> MilitaryUnit.fromJson(mu)
        ).toMap
      )).getOrElse(None),
    )
}
