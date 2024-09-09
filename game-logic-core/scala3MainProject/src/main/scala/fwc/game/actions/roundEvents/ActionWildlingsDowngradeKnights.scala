package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsDowngradeKnights
import ujson.Value

case class ActionWildlingsDowngradeKnights(
                                            gameState: GameState,
                                            houseType: HouseType,
                                            tileNumber: TileNumber
                                          )
  extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsDowngradeKnights]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseWildlingsDowngradeKnights]

    if !currentPhase.houseTypes.contains(houseType)
    then throw new ActionException("Wrong house")

    val knight = MilitaryUnit(
      houseType,
      MilitaryUnitType.Knights
    )

    if !gameState.armies(tileNumber).contains(knight)
    then throw new ActionException(s"There is no knight of house ${houseType} at this tile (${gameRules.board(tileNumber).name})")

    val updatedArmies = gameState.armies.disbandMilitaryUnit(tileNumber, knight)

    val numFootmen = updatedArmies.foldLeft(0)(
      (acc, cur: (TileNumber, Seq[MilitaryUnit])) =>
        acc + cur._2.count(mu => mu.unitType == MilitaryUnitType.Footmen && mu.house == houseType)
    )

    val updatedArmies2 =
      if numFootmen < gameRules.maxArmies(MilitaryUnitType.Footmen)
      then updatedArmies + (tileNumber -> (updatedArmies(tileNumber) :+ MilitaryUnit(houseType, MilitaryUnitType.Footmen)))
      else updatedArmies

    val updatedPhase =
      if currentPhase.houseTypes(houseType) == 1
      then currentPhase.copy(
        currentPhase.houseTypes - houseType
      )
      else currentPhase.copy(
        currentPhase.houseTypes + (houseType -> (currentPhase.houseTypes(houseType) - 1))
      )

    val newPhase =
      if currentPhase.houseTypes.isEmpty
      then WildlingsCards.getNextNonWildlingsPhase(
        gameState.wildlingsStartedFrom12Points.head,
        gameState.tracks,
        gameState.boardCards,
        gameState.wildlingCounter,
      )
      else updatedPhase

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies2,
      wildlingsStartedFrom12Points =
        if currentPhase.houseTypes.isEmpty
        then None
        else gameState.wildlingsStartedFrom12Points
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsDowngradeKnights",
    "houseType" -> houseType.toString,
    "tileNumber" -> tileNumber
  )
}

object ActionWildlingsDowngradeKnights extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsDowngradeKnights =
    ActionWildlingsDowngradeKnights(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt
    )
}