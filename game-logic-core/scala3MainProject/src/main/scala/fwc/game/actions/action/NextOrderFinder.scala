package fwc.game.actions.action

import fwc.game.{FWCException, GameState, gameRules}
import fwc.game.board.{TrackType, Tracks}
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseResolveConsolidatePowerOrder, SubPhaseResolveMarchOrder, SubPhaseResolveRaidOrder, SubPhaseResolveSpecialConsolidatePower}
import fwc.game.planningPhase.{Order, OrderType, PlacedOrders}

import scala.annotation.tailrec

object NextOrderFinder {

  def nextSubPhase(
                    gameState: GameState,
                    orderTypeToSearch: OrderType,
                    lastHouseWhoResolvedOrder: HouseType = null
                   ): SubPhase = {
    val nextOpt = NextOrderFinder.next(gameState.tracks, gameState.placedOrders, orderTypeToSearch, lastHouseWhoResolvedOrder)
    val next = if nextOpt.nonEmpty
    then nextOpt.head
    else return SubPhaseResolveConsolidatePowerOrder()
    NextOrderFinder.buildSubPhase(next._1, next._2)
  }

  private def next(
            tracks: Tracks,
            placedOrders: PlacedOrders,
            orderTypeToSearch: OrderType,
            lastHouseWhoResolvedOrder: HouseType = null
          ): Option[(HouseType, OrderType)] = {
    
    @tailrec
    def reorderThroneTrack(originalTrack: Seq[HouseType], newTrack: Seq[HouseType] = Seq()): Seq[HouseType] =
      val updatedNewTrack = newTrack :+ originalTrack.head
      if originalTrack.head == lastHouseWhoResolvedOrder
      then originalTrack.tail :++ updatedNewTrack
      else reorderThroneTrack(originalTrack.tail, updatedNewTrack)

    
    val tempThroneTrack = 
      if lastHouseWhoResolvedOrder == null
      then tracks(TrackType.Throne)
      else reorderThroneTrack(tracks(TrackType.Throne))
    
    val flatOrders = (
      for (
        poh <- placedOrders.placedOrders;
        ord <- poh._2
      ) yield (ord._1, poh._1, ord._2)
      ).toSeq

    val searchFunc = findRaidByType(flatOrders)

    val raidHouseOrder = searchFunc(OrderType.Raid, _._3.orderType == OrderType.Raid, tempThroneTrack)

    if raidHouseOrder.nonEmpty
    then return raidHouseOrder

    val marchOrderFilter = (t: (Int, HouseType, Order)) => t._3.orderType == OrderType.March

    val marchHouseOrder = if orderTypeToSearch == OrderType.March
    then searchFunc(OrderType.March, marchOrderFilter, tempThroneTrack)
    else searchFunc(OrderType.March, marchOrderFilter, tracks(TrackType.Throne))

    if marchHouseOrder.nonEmpty
    then return marchHouseOrder

    val specialConsPowerOrderFilter = (t: (Int, HouseType, Order)) =>
      t._3.orderType == OrderType.ConsolidatePower
      && t._3.isStar
      && gameRules.board(t._1).musteringPoints > 0

    val consPowerHouseOrder = if orderTypeToSearch == OrderType.ConsolidatePower
    then searchFunc(OrderType.ConsolidatePower, specialConsPowerOrderFilter, tempThroneTrack)
    else searchFunc(OrderType.ConsolidatePower, specialConsPowerOrderFilter, tracks(TrackType.Throne))

    consPowerHouseOrder
  }

  private def findRaidByType(
                      flatOrders: Seq[(Int, HouseType, Order)]
                    )
                    (
                      orderType: OrderType,
                      filter:((Int, HouseType, Order)) => Boolean,
                      sortedHouses: Seq[HouseType]
                    ): Option[(HouseType, OrderType)] = {
    val filteredOrders = flatOrders.filter(filter).sortWith((a, b) =>
      sortedHouses.indexOf(a._2) < sortedHouses.indexOf(b._2)
    )

    if filteredOrders.nonEmpty
    then Some((filteredOrders.head._2, orderType))
    else None
  }

  private def buildSubPhase(houseType: HouseType, orderType: OrderType): SubPhase = {
    orderType match
      case OrderType.Raid => SubPhaseResolveRaidOrder(houseType)
      case OrderType.March => SubPhaseResolveMarchOrder(houseType)
      case OrderType.ConsolidatePower => SubPhaseResolveSpecialConsolidatePower(houseType)
      case other => throw new FWCException(s"Invalid order type ${other.toString}")
  }
}
