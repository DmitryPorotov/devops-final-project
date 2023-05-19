package fwc.gameSaving.actions.roundEvents

import fwc.game.board.{TrackCourt, TrackFiefdoms, TrackThrone, TrackType, Tracks}
import fwc.game.eventsPhase.cards.BoardCards
import fwc.game.houses.HouseType
import fwc.game.phases.SubPhase
import fwc.game.phases.roundEventsSubPhases.*
import fwc.game.planningPhase.*
import fwc.gameLoading.RoundEventCard
import fwc.gameSaving.actions.ActionException

object EventCards {
  def getPhaseForDeck1Card(card1: RoundEventCard, throneOwner: HouseType): SubPhase =
    card1.code match
      case 0 => SubPhaseRecalculateSupplies()
      case 1 => SubPhaseMuster(throneOwner)
      case 2 => SubPhaseChooseUpdateSupplyOrMuster(throneOwner)
      case 3 => SubPhaseGetEventCards()
      case 4 => null
      case c => throw new ActionException(s"Unknown card code $c for deck 1")
      
  def getPhaseForDeck2Card(card2: RoundEventCard, ravenOwner: HouseType): SubPhase =
    card2.code match
      case 0 => SubPhaseCollectTaxes()
      case 1 => SubPhaseTracksBids(HouseType.getSeqOfAll, TrackThrone)
      case 2 => SubPhaseChooseTracksBidsOrCollectTaxes(ravenOwner)
      case 3 => SubPhaseGetEventCards()
      case 4 => null
      case c => throw new ActionException(s"Unknown card code $c for deck 2")

  def getPhaseForDeck3Card(card3: RoundEventCard, steelBladeOwner: HouseType): SubPhase =
    card3.code match
      case 0 => SubPhaseWildlingsBids(HouseType.getSeqOfAll, 6, false)
      case 1 => SubPhaseChooseDisableMarchPlus1OrDefendOrders(steelBladeOwner)
      case 2 => SubPhaseDisableOrder(OrderMarch)
      case 3 => SubPhaseDisableOrder(OrderSupport)
      case 4 => SubPhaseDisableOrder(OrderDefend)
      case 5 => SubPhaseDisableOrder(OrderConsolidatePower)
      case 6 => SubPhaseDisableOrder(OrderRaid)
      case c => throw new ActionException(s"Unknown card code $c for deck 3")

  def fallThroughFromDeck1(
                        tracks: Tracks,
                        boardCards: BoardCards
                      ): SubPhase = {
    val c = getPhaseForDeck1Card(boardCards.roundEvents1.head, tracks.throneOwner)
    if c == null
    then fallThroughFromDeck2(tracks, boardCards)
    else c
  }
  def fallThroughFromDeck2(
                        tracks: Tracks,
                        boardCards: BoardCards
                      ): SubPhase = {
    val c = getPhaseForDeck2Card(boardCards.roundEvents2.head, tracks.ravenOwner)
    if c == null
    then getPhaseForDeck3Card(boardCards.roundEvents3.head, tracks.steelBladeOwner)
    else c
  }

  def bidsFallThroughFromThrone(trackType: TrackType, card3: RoundEventCard, steelBladeOwner: HouseType): SubPhase = {
    if trackType == TrackThrone
    then SubPhaseTracksBids(HouseType.getSeqOfAll, TrackFiefdoms)
    else if trackType == TrackFiefdoms
    then SubPhaseTracksBids(HouseType.getSeqOfAll, TrackCourt)
    else EventCards.getPhaseForDeck3Card(card3, steelBladeOwner)
  }
}
