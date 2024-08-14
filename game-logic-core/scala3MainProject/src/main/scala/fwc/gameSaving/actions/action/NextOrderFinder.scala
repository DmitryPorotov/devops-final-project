package fwc.gameSaving.actions.action

import fwc.game.{FWCException, GameState, gameRules}
import fwc.game.board.{TrackType, Tracks}
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseCleanUpAfterRound, SubPhaseResolveConsolidatePowerOrder, SubPhaseResolveMarchOrder, SubPhaseResolveRaidOrder, SubPhaseResolveSpecialConsolidatePower}
import fwc.game.planningPhase.{Order, OrderConsolidatePower, OrderMarch, OrderRaid, OrderType, PlacedOrders}

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

    val raidHouseOrder = searchFunc(OrderRaid, _._3.orderType == OrderRaid, tempThroneTrack)

    if raidHouseOrder.nonEmpty
    then return raidHouseOrder

    val marchOrderFilter = (t: (Int, HouseType, Order)) => t._3.orderType == OrderMarch

    val marchHouseOrder = if orderTypeToSearch == OrderMarch
    then searchFunc(OrderMarch, marchOrderFilter, tempThroneTrack)
    else searchFunc(OrderMarch, marchOrderFilter, tracks(TrackType.Throne))

    if marchHouseOrder.nonEmpty
    then return marchHouseOrder

    val specialConsPowerOrderFilter = (t: (Int, HouseType, Order)) =>
      t._3.orderType == OrderConsolidatePower
      && t._3.isStar
      && gameRules.board(t._1).musteringPoints > 0

    val consPowerHouseOrder = if orderTypeToSearch == OrderConsolidatePower
    then searchFunc(OrderConsolidatePower, specialConsPowerOrderFilter, tempThroneTrack)
    else searchFunc(OrderConsolidatePower, specialConsPowerOrderFilter, tracks(TrackType.Throne))

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
      case OrderRaid => SubPhaseResolveRaidOrder(houseType)
      case OrderMarch => SubPhaseResolveMarchOrder(houseType)
      case OrderConsolidatePower => SubPhaseResolveSpecialConsolidatePower(houseType)
      case other => throw new FWCException(s"Invalid order type ${other.toString}")
  }
}
