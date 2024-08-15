package fwc.gameSaving.actions.planning

import fwc.JsonSerializable
import fwc.game.gameRules
import fwc.game.{FWCException, GameState}
import fwc.game.board.{MilitaryUnit, TileNumber, Tracks}
import fwc.game.houses.HouseType
import fwc.game.phases.{SubPhase, planningSubPhases}
import fwc.game.phases.actionSubPhases.{SubPhaseResolveMarchOrder, SubPhaseResolveRaidOrder, SubPhaseResolveSpecialConsolidatePower}
import fwc.game.phases.planningSubPhases.{SubPhaseAddOrder, SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard, SubPhaseReadyToOpenOrders}
import fwc.game.planningPhase.{Order, PlacedOrders}
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import ujson.Value

case class ActionOpenOrders(
                             gameState: GameState,
                             houseType: HouseType
                           ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseAddOrder]
    then throw new ActionException("Wrong phase")

    val currentPhase = gameState.subPhase.asInstanceOf[SubPhaseAddOrder]

    if !currentPhase.houseTypes.contains(houseType)
    then throw new ActionException("You have already confirmed your orders")

    val ordersOfHouse = gameState.placedOrders(houseType)
    val noOrderArmies: Map[TileNumber, Seq[MilitaryUnit]] =
      gameState.armies.filter(
        (tileNumber, army: Seq[MilitaryUnit]) =>
          army.head.house == houseType && !ordersOfHouse.contains(tileNumber)
          && army.exists(_.unitType.canBeMustered)
      )

    val newSubPhase =
      if noOrderArmies.isEmpty || (noOrderArmies.nonEmpty && !gameState.availableOrders.hasAvailableOrders(houseType, gameState.tracks))
      then
        currentPhase.copy(
          houseTypes = currentPhase.houseTypes.filter(_ != houseType)
        )
      else throw new ActionException(s"You have armies without orders at tiles [${noOrderArmies.map(_._1.toString).mkString(",")}]")

    val updatedSubPhase =
      if newSubPhase.houseTypes.isEmpty then
        SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard(gameState.tracks.ravenOwner)
      else newSubPhase

    gameState.copy(
      subPhase = updatedSubPhase
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> ujson.Str("openOrders"),
    "houseType" -> ujson.Str(houseType.toString),
  )
}

object ActionOpenOrders extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionOpenOrders = {
    ActionOpenOrders(
      gameState,
      HouseType.fromString(json("houseType").str)
    )
  }
}
