package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.board.{Armies, MilitaryUnit, TileNumber, TrackType}
import fwc.game.{GameState, gameRules}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.*
import fwc.game.planningPhase.Order
import ujson.Value

import scala.util.Try

case class ActionResolveSupportOrder(
                                      gameState: GameState,
                                      fromHouseType: HouseType,
                                      toHouseType: HouseType,
                                      tileNumbers: Seq[TileNumber]
                                    ) extends Action(gameState) with PlayerAction(fromHouseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseResolveSupportOrder]
    then throw new ActionException("Wrong phase")

    val targetTile = gameRules.board(gameState.combat.defenderTileNum)
    val supportingTiles = tileNumbers.map(tn => gameRules.board(tn))

    val currentSubPhase = gameState.subPhase.asInstanceOf[SubPhaseResolveSupportOrder]
    if currentSubPhase.houseType != fromHouseType
    then throw new ActionException(s"House $fromHouseType has no support orders in near ${targetTile.name}.")

    if toHouseType == HouseType.Neutral
    then throw new ActionException("Can not support neutral house.")

    supportingTiles.foreach(
      tn =>
        if !currentSubPhase.tilesNumbers.contains(tn.number)
        then throw new ActionException(s"Tile ${tn.name} has no support order of house ${fromHouseType}.")
        if gameState.combat.defenderSupport.contains(tn.number)
        then throw new ActionException(s"Army of tile ${tn.name} is already supporting house ${gameState.combat.defenderHouse}.")
        if gameState.combat.attackerSupport.contains(tn.number)
        then throw new ActionException(s"Army of tile ${tn.name} is already supporting house ${gameState.combat.attackerHouse}.")
    )
    
    if (fromHouseType == gameState.combat.attackerHouse && toHouseType == gameState.combat.defenderHouse)
      || (fromHouseType == gameState.combat.defenderHouse && toHouseType == gameState.combat.attackerHouse)
      then throw new ActionException("Cannot support against yourself.")

    val supportOrdersForTile = gameState.placedOrders.getSupportOrdersForTile(gameState.combat.defenderTileNum)
    val updatedCombat = gameState.combat.copy(
      assignedSupportTiles = gameState.combat.assignedSupportTiles ++ tileNumbers
    )
    val remainingSupportOrdersTiles = supportOrdersForTile.filter(x =>
      !updatedCombat.assignedSupportTiles.contains(x._1)
    )
    try {
      val newPhase = CombatCommon.getNewSubPhaseForMarchSupport(
        remainingSupportOrdersTiles,
        gameState.tracks(TrackType.Throne),
        updatedCombat.attackerHouse,
        updatedCombat.defenderHouse
      )

      if toHouseType != null
      then {
        if toHouseType != updatedCombat.defenderHouse && toHouseType != updatedCombat.attackerHouse
        then throw new ActionException(s"Support order should be targeted to ${updatedCombat.defenderHouse} or " +
          s"${updatedCombat.attackerHouse}")
        val updatedCombat1 = if toHouseType == updatedCombat.defenderHouse
        then updatedCombat.copy(
            defenderSupport = gameState.combat.defenderSupport :++ tileNumbers
          )
        else updatedCombat.copy(
          attackerSupport = gameState.combat.defenderSupport :++ tileNumbers
        )
        gameState.copy(
          subPhase = newPhase,
          combat = updatedCombat1
        )
      }
      else gameState.copy(
        subPhase = newPhase
      )
    }
    catch {
      case _: AttackNeutralException =>
        CombatCommon.attackNeutrals(gameState)
      case e: Throwable => throw e
    }

  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveSupportOrder",
    "fromHouseType" -> fromHouseType.toString,
    "toHouseType" -> toHouseType.toString,
    "tileNumbers" -> ujson.Arr.from(tileNumbers)
  )
}

object ActionResolveSupportOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveSupportOrder =
    ActionResolveSupportOrder(
      gameState,
      HouseType.fromString(json("fromHouseType").str),
      Try(HouseType.fromString(json("toHouseType").str)).getOrElse(null),
      json("tileNumbers").arr.map(_.num.toInt).toSeq
    )
}
