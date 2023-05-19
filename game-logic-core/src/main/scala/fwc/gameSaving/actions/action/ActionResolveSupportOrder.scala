package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitSiegeEngines, TileNumber, TrackThrone}
import fwc.game.{GameState, gameRules}
import fwc.game.houses.{HouseNeutral, HouseType}
import fwc.game.phases.actionSubPhases.*
import fwc.game.planningPhase.{Order, OrderMarch}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

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
    if currentSubPhase.houseType == fromHouseType
    then throw new ActionException(s"House $fromHouseType has no support orders in near ${targetTile.name}")

    if toHouseType == HouseNeutral
    then throw new ActionException("Can not support neutral house")

    supportingTiles.foreach(
      tn =>
        if !currentSubPhase.tilesNumbers.contains(tn)
        then throw new ActionException(s"Tile ${tn.name} has no support order")
    )

    val supportOrdersFromHouse = gameState.placedOrders.getSupportOrdersForTile(gameState.combat.defenderTileNum)
      .foldLeft(Seq())(
        (acc: Seq[Int], tnHouseOrder: (TileNumber, (HouseType, Order))) =>
          if tnHouseOrder._2 == fromHouseType
          then acc :+ tnHouseOrder._1
          else acc
      )
    val remainingSupportOrdersTiles = currentSubPhase.tilesNumbers.filter(tn => !supportOrdersFromHouse.contains(tn))
    try {
      val newPhase = CombatCommon.getNewSubPhaseForMarchSupport(
        gameState.placedOrders.getSupportOrdersForTile(gameState.combat.defenderTileNum)
          .filter((tn, _) => remainingSupportOrdersTiles.contains(tn)),
        gameState.tracks(TrackThrone),
        gameState.combat.defenderHouse
      )

      if toHouseType != null
      then {
        if toHouseType != gameState.combat.defenderHouse && toHouseType != gameState.combat.attackerHouse
        then throw new ActionException(s"Support order should be targeted to ${gameState.combat.defenderHouse} or " +
          s"${gameState.combat.attackerHouse}")
        val updatedCombat = if toHouseType == gameState.combat.defenderHouse
        then gameState.combat.copy(
            defenderSupport = gameState.combat.defenderSupport :++ tileNumbers
          )
        else gameState.combat.copy(
          attackerSupport = gameState.combat.defenderSupport :++ tileNumbers
        )
        gameState.copy(
          subPhase = newPhase,
          combat = updatedCombat
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
      HouseType.fromString(json("toHouseType").str),
      json("tileNumbers").arr.map(_.num.toInt).toSeq
    )
}
