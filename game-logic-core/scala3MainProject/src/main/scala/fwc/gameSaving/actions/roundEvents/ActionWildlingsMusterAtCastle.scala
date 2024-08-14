package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.eventsPhase.Mustering
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseMuster, SubPhaseWildlingsMusterAtCastle}
import fwc.gameSaving.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionWildlingsMusterAtCastle(
                                          gameState: GameState,
                                          houseType: HouseType,
                                          sourceTile: TileNumber,
                                          targetUnits: Seq[(TileNumber, Boolean, MilitaryUnit)]
                                        ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsMusterAtCastle]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseWildlingsMusterAtCastle].houseType != houseType
    then throw new ActionException("Wrong house")

    val updatedGameState =
      gameState.copy(
        subPhase = WildlingsCards.getNextNonWildlingsPhase(
          gameState.wildlingsStartedFrom12Points.head,
          gameState.tracks,
          gameState.boardCards
        ),
        wildlingsStartedFrom12Points = None
      )

    if targetUnits.isEmpty
    then return updatedGameState

    val musteringPointsAtSource = gameRules.board(sourceTile).musteringPoints

    val musteringPointsNeeded = targetUnits.foldLeft(0)(
      (acc, cur) =>
        acc + {
          if cur._3.unitType.musteringPoints < 0
          then throw new ActionException("Can not muster garrison or power token")

          if cur._3.unitType.musteringPoints == 2 && cur._2
          then 1
          else cur._3.unitType.musteringPoints
        }
    )

    if musteringPointsNeeded > musteringPointsAtSource
    then throw new ActionException(s"Not enough mustering points at source (${gameRules.board(sourceTile).name})")

    targetUnits.foldLeft(updatedGameState)(
      (acc: GameState, cur: (TileNumber, Boolean, MilitaryUnit)) => {
        val (armies: Armies, _) =
          if cur._3.unitType == MilitaryUnitType.Ships
          then Mustering.musterShips(sourceTile, cur._1, cur._3, acc)
          else Mustering.musterGroundUnit(sourceTile, cur._3, acc, cur._2)

        acc.copy(armies = armies)
      }
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsMusterAtCastle",
    "houseType" -> houseType.toString,
    "sourceTile" -> sourceTile,
    "targetUnits" -> ujson.Arr.from(
      targetUnits.map(
        (tnUpMu: (TileNumber, Boolean, MilitaryUnit)) =>
          ujson.Arr.from(
            Seq(
            ujson.Num(tnUpMu._1),
            ujson.Bool(tnUpMu._2),
            tnUpMu._3.toJson
          )
        )
      )
    )
  )
}

object ActionWildlingsMusterAtCastle extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsMusterAtCastle =
    ActionWildlingsMusterAtCastle(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("sourceTile").num.toInt,
      json("targetUnits").arr.map(
        tnUpMu =>
          (
            tnUpMu.arr.head.num.toInt,
            tnUpMu.arr.tail.head.bool,
            MilitaryUnit.fromJson(tnUpMu.arr.tail.tail.head)
          )
      ).toSeq
    )
}