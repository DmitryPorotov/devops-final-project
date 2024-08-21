package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.eventsPhase.PowerTokens
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.SubPhaseResolveRaidOrder
import fwc.game.planningPhase.{Order, OrderType}
import fwc.game.gameRules
import fwc.gameLoading.BoardTileType
import ujson.Value

case class ActionResolveRaidOrder(
                                   gameState: GameState,
                                   houseType: HouseType,
                                   sourceTileNumber: Int,
                                   targetTileNumber: Int
                                 ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseResolveRaidOrder]
    then throw new ActionException("Wrong phase")

    if gameState.subPhase.asInstanceOf[SubPhaseResolveRaidOrder].houseType != houseType
    then throw new ActionException("Wrong house")

    val sourceOrderOpt = gameState.placedOrders.getOrderByTileNumber(sourceTileNumber)
    val sourceOrder = if sourceOrderOpt.isEmpty
      || sourceOrderOpt.head._2.orderType != OrderType.Raid
      || sourceOrderOpt.head._1 != houseType
    then throw new ActionException(s"There is no raid order of house \"$houseType\" in the source tile")
    else sourceOrderOpt.head

    val sourceTile = gameRules.board(sourceTileNumber)
    val targetTile = gameRules.board(targetTileNumber)

    if sourceTileNumber == targetTileNumber
    then
      val updatedGameState = gameState.copy(
        placedOrders = gameState.placedOrders.removeOrder(houseType, sourceTileNumber)
      )
      return updatedGameState.copy(
        subPhase = NextOrderFinder.nextSubPhase(updatedGameState, OrderType.Raid, houseType)
      )
    else if !sourceTile.isNeighbourOf(targetTile)
    then throw new ActionException(s"Tile ${sourceTile.name} is not a not a neighbour of ${targetTile.name}")

    val targetOrderOpt = gameState.placedOrders.getOrderByTileNumber(targetTileNumber)

    val targetOrder = if targetOrderOpt.isEmpty
    then throw new ActionException(s"Tile ${targetTile.name} has no order")
    else targetOrderOpt.head

    if sourceTile.tileType == BoardTileType.Land && targetTile.tileType == BoardTileType.Sea
    then throw new ActionException(s"Can not raid sea (${targetTile.name}) from land (${sourceTile.name})")

    if sourceTile.tileType == BoardTileType.Port && targetTile.tileType == BoardTileType.Land
    then throw new ActionException(s"Can not raid land (${targetTile.name}) from port (${sourceTile.name})")

    if targetOrder._1 == houseType
    then throw new ActionException("Can not remove own order")

    if targetOrder._2.orderType == OrderType.March
    then throw new ActionException("Can not remove a march order")

    if targetOrder._2.orderType == OrderType.Defend && !sourceOrder._2.isStar
    then throw new ActionException("Can not remove a defend order using non-special raid order")

    val newPowerTokens = if targetOrder._2.orderType == OrderType.ConsolidatePower
    then gameState.powerTokens.transferOneToken(targetOrder._1, houseType, gameState.armies)
    else gameState.powerTokens

    val updatedGameState = gameState.copy(
      powerTokens = newPowerTokens,
      placedOrders = gameState.placedOrders
        .removeOrder(sourceOrder._1, sourceTileNumber)
        .removeOrder(targetOrder._1, targetTileNumber)
    )

    val newSubPhase = NextOrderFinder.nextSubPhase(updatedGameState, OrderType.Raid, houseType)
    updatedGameState.copy(
      subPhase = newSubPhase,
    )
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "resolveRaidOrder",
    "houseType" -> ujson.Str(houseType.toString),
    "sourceTileNumber" -> ujson.Num(sourceTileNumber),
    "targetTileNumber" -> ujson.Num(targetTileNumber)
  )
}

object ActionResolveRaidOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionResolveRaidOrder =
    ActionResolveRaidOrder(
      gameState,
      HouseType.fromString(json("houseType").str),
      json("sourceTileNumber").num.toInt,
      json("targetTileNumber").num.toInt
    )
}
