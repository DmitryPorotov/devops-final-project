package fwc.game.actions.roundEvents

import fwc.JsonSerializable
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.actions.roundEvents.wildlingsCards.WildlingsCards
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, TileNumber, isValid}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsKillUnits
import ujson.Value

case class ActionWildlingsKillUnit(
                                    gameState: GameState,
                                    houseType: HouseType,
                                    tileNumber: TileNumber,
                                    unit: MilitaryUnit,
                                  ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseWildlingsKillUnits]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseWildlingsKillUnits]

    if !tileNumber.isValid
    then throw new ActionException("Invalid tile number")

    if !currentPhase.houseTypes.exists(_._1 == houseType)
    then throw new ActionException("Wrong house")

    val doPrioritizeCastles =
      currentPhase.loserHouse.contains(houseType) && gameState.boardCards.wildlings.head.code == 6
      
    val tilesWithCastlesContainingArmiesOfThisHouse: Seq[TileNumber] =
      gameState.armies.foldLeft(Seq[TileNumber]())(
        (acc, cur: (TileNumber, Seq[MilitaryUnit])) => 
          if gameRules.board(cur._1).musteringPoints > 0 
            && cur._2.exists(mu => mu.house == houseType && mu.unitType.canBeMustered)
          then acc :+ cur._1
          else acc
      )
    
    if doPrioritizeCastles 
      && tilesWithCastlesContainingArmiesOfThisHouse.nonEmpty 
      && !tilesWithCastlesContainingArmiesOfThisHouse.contains(tileNumber)
    then throw new ActionException("Kill units at castles or strongholds first.")
    
    val updatedArmies = gameState.armies.disbandMilitaryUnit(tileNumber, unit)

    val updatedPhaseHouses =
      val sum = currentPhase.houseTypes(houseType) - 1
      if sum <= 0
      then currentPhase.houseTypes - houseType
      else currentPhase.houseTypes + (houseType -> sum)

    val newPhase =
      if updatedPhaseHouses.isEmpty
      then WildlingsCards.getNextNonWildlingsPhase(
        gameState.wildlingsStartedFrom12Points.head, 
        gameState.tracks, 
        gameState.boardCards,
        gameState.wildlingCounter,
      )
      else currentPhase.copy(houseTypes = updatedPhaseHouses)

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies,
      wildlingsStartedFrom12Points =
        if updatedPhaseHouses.isEmpty
        then None
        else gameState.wildlingsStartedFrom12Points
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "wildlingsKillUnit",
    "houseType" -> houseType.toString,
    "tileNumber" -> tileNumber,
    "unit" -> unit.toJson
  )
}

object ActionWildlingsKillUnit extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionWildlingsKillUnit =
    ActionWildlingsKillUnit(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("tileNumber").num.toInt,
      MilitaryUnit.fromJson(json("unit"))
    )
}