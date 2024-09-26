package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseMuster, SubPhaseWildlingsUpgradeKnights}
import ujson.Value

import scala.util.Try

case class ActionWildlingsUpgradeKnights(
                                          gameState: GameState,
                                          houseType: HouseType,
                                          tileNumber1: TileNumber,
                                          tileNumber2: Option[TileNumber],
                                        ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsUpgradeKnights]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseWildlingsUpgradeKnights].houseType != houseType
    then throw new ActionException("Wrong house")

    val footman = MilitaryUnit(
      houseType,
      MilitaryUnitType.Footmen
    )

    if !gameState.armies(tileNumber1).contains(footman)
    then throw new ActionException(s"There is no footman to upgrade at tile $tileNumber1 (${gameRules.board(tileNumber1).name})")

    if tileNumber2.nonEmpty && !gameState.armies(tileNumber2.head).contains(footman)
    then throw new ActionException(s"There is no footman to upgrade at tile ${tileNumber2.head} " +
      s"(${gameRules.board(tileNumber2.head).name})")

    val numKnights = gameState.armies.countUnitsByTypeAndHouse(MilitaryUnitType.Knights, houseType)

    if numKnights > (gameRules.maxArmies(MilitaryUnitType.Knights) - 1) && tileNumber2.nonEmpty
    then throw new ActionException("You can only upgrade one footman to a knight")

    val updatedArmies = gameState.armies.disbandMilitaryUnit(tileNumber1, footman)
    val updatedArmies1 = updatedArmies + (tileNumber1 -> (updatedArmies.getOrElse(tileNumber1, Seq()) :+ MilitaryUnit(houseType, MilitaryUnitType.Knights)))

    val updatedArmies2 =
      if tileNumber2.nonEmpty
      then  updatedArmies1.disbandMilitaryUnit(tileNumber2.head, footman)
      else updatedArmies1

    val updatedArmies3 =
      if tileNumber2.nonEmpty
      then updatedArmies2 + (tileNumber2.head -> (updatedArmies2.getOrElse(tileNumber2.head, Seq()) :+ MilitaryUnit(houseType, MilitaryUnitType.Knights)))
      else updatedArmies1

    gameState.copy(
      subPhase = WildlingsCards.getNextNonWildlingsPhase(
        gameState.wildlingsStartedFrom12Points.head,
        gameState.tracks,
        gameState.boardCards,
        gameState.wildlingCounter,
      ),
      armies = updatedArmies3,
      wildlingsStartedFrom12Points = None
    )
  }

  override def toJson: Value =
    val json = ujson.Obj(
      Action.actionTypeJsonKey -> "wildlingsUpgradeKnights",
      "houseType" -> houseType.toString,
      "tileNumber1" -> tileNumber1
    )
    if tileNumber2.nonEmpty
    then json.obj.addOne("tileNumber2" -> tileNumber2.head)
    json
}

object ActionWildlingsUpgradeKnights extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsUpgradeKnights =
    ActionWildlingsUpgradeKnights(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber1").num.toInt,
      Try(json("tileNumber2").numOpt.map(_.toInt)).getOrElse(None)
    )
}
