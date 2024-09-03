package fwc.game.actions.planning

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.Action.PlanningActions
import fwc.game.actions.{Action, ActionException, JsonParsableAction, PlayerAction}
import fwc.game.board.{TileNumber, TrackType, isValid}
import fwc.game.houses.HouseType
import fwc.game.phases.planningSubPhases.SubPhaseAddOrder
import fwc.game.planningPhase.Order


case class ActionAddOrder(
                           gameState: GameState,
                           houseType: HouseType,
                           order: Order,
                           tileNumber: TileNumber
                         ) extends Action(gameState) with PlayerAction(houseType) with JsonSerializable {
  override def doAction(): GameState = {
    if !gameState.subPhase.isInstanceOf[SubPhaseAddOrder]
    then throw new ActionException("Wrong phase")
    
    if !gameState.subPhase.asInstanceOf[SubPhaseAddOrder].houseTypes.contains(houseType)
    then throw new ActionException("You have already confirmed your orders")

    if !tileNumber.isValid
    then throw new ActionException(s"Invalid tile number $tileNumber")
    
    doPlaceOrder()
  }

  def doPlaceOrder(): GameState = {
    if !gameState.armies.hasCommandableHouseArmyOnTile(tileNumber, houseType)
    then throw new ActionException(s"This tile does not contain an army of house $houseType")
    
    val placedOrders = gameState.placedOrders.placeOrder(
      houseType,
      tileNumber,
      order,
      gameState.tracks(TrackType.Court).indexOf(houseType)
    )

    val availableOrders = gameState.availableOrders.useOrder(houseType, order)

//    val flatPo: Map[TileNumber, Order] = placedOrders.placedOrders.flatMap[Int, Order]((_, orders: Map[Int, Order]) => orders)
//    val noOrderArmies: Map[TileNumber, Seq[MilitaryUnit]] =
//      gameState.armies.filter(
//        (tileNumber, army: Seq[MilitaryUnit]) =>
//          !flatPo.contains(tileNumber) && army.head.house != HouseNeutral
//          && army.exists(_.unitType.canBeMustered)
//      )
//
//    val houses = noOrderArmies
//      .map[HouseType]((_, armies) => armies.head.house)
//      .toSet
//      .filter(ht => !availableOrders.hasAvailableOrders(ht, gameState.tracks))
//
//    val subPhase =
//      if houses.isEmpty
//      then SubPhaseReadyToOpenOrders(HouseType.getSeqOfAll)
//      else SubPhaseAddOrder(houses.toSeq)


    gameState.copy(
//      subPhase = subPhase,
      placedOrders = placedOrders,
      availableOrders = availableOrders
    )
  }

  def toJson: ujson.Value = ujson.Obj(
      Action.actionTypeJsonKey -> PlanningActions.AddOrder.string,
      "order" -> (if order == null then ujson.Null else order.toJson),
      "houseType" -> houseType.toString,
      "tileNumber" -> tileNumber
    )
  
}

object ActionAddOrder extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: ujson.Value): ActionAddOrder = {
    ActionAddOrder(
      gameState,
      HouseType.fromString(json("houseType").str),
      Order.fromJson(json("order")),
      json("tileNumber").num.toInt
    )
  }
}
