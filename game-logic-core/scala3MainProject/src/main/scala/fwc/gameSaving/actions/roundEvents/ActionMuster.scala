package fwc.gameSaving.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.board.TrackType
import fwc.game.{GameState, gameRules}
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.eventsPhase.{Mustering, UsedMusteringPoints}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseMuster
import fwc.game.planningPhase.OrderType
import fwc.gameSaving.actions.action.NextOrderFinder
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

import scala.util.Try


case class ActionMuster(
                         gameState: GameState,
                         houseType: HouseType,
                         unitToMuster: MilitaryUnit,
                         fromTile: TileNumber,
                         toTile: Option[TileNumber],
                         isUpgrade: Boolean
                       ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseMuster]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseMuster].houseType != houseType
    then throw new ActionException("Wrong house")

    val (ar: Armies, usedMustPoints: UsedMusteringPoints) =
      if unitToMuster.unitType == MilitaryUnitType.Ships
      then if toTile.isEmpty
        then throw new ActionException("toTile should not be empty")
        else Mustering.musterShips(fromTile, toTile.head, unitToMuster, gameState)
      else Mustering.musterGroundUnit(fromTile, unitToMuster, gameState, isUpgrade)

    gameState.copy(
      armies = ar,
      usedMusteringPoints = usedMustPoints
    )
  }

  override def toJson: Value =
    val json = ujson.Obj(
      Action.actionTypeJsonKey -> "muster",
        "houseType" -> houseType.toString,
        "unitToMuster" -> unitToMuster.toJson,
        "fromTile" -> fromTile
      )
    if toTile.nonEmpty
    then json.obj.addOne("toTile" -> toTile.head)
    if isUpgrade
    then json.obj.addOne("isUpgrade" -> isUpgrade)
    json
}

object ActionMuster extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionMuster =
    ActionMuster(
      gameState,
      HouseType.fromString(json("houseType").str),
      MilitaryUnit.fromJson(json("unitToMuster")),
      json("fromTile").num.toInt,
      json.obj.get("toTile").map(_.num.toInt),
      Try(json("isUpgrade").bool).getOrElse(false)
    )
}
