package fwc.gameSaving.actions.roundEvents.wildlingsCards

import fwc.game.GameState
import fwc.game.board.{TrackType, Tracks}
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.{SubPhaseWildlingsChooseTrackToBeFirstAt, SubPhaseWildlingsChooseTrackToBeLastAt}

case class Card8(gameState: GameState) extends WildlingsCards(gameState) {
  extension (ht1: HouseType)
    def isHigherOnThroneTrackThan(ht2: HouseType): Boolean =
      ht1.isHigherOnTrack(gameState.tracks(TrackType.Throne))(ht2)

  override def resolve(): GameState = {
    if phase.isWin
    then gameStateWithCounterAndBids.copy(
      subPhase = SubPhaseWildlingsChooseTrackToBeFirstAt(phase.loserWinnerHouse)
    )
    else gameStateWithCounterAndBids.copy(
      subPhase = SubPhaseWildlingsChooseTrackToBeLastAt(
        phase.houseTypes.filter(_ != phase.loserWinnerHouse).sortWith(
          (a, b) => a isHigherOnThroneTrackThan b
        )
      ),
      tracks =
        gameState.tracks.foldLeft(gameState.tracks)(
          (acc: Tracks, cur: (TrackType, Seq[HouseType])) =>
            acc.setHouseLowestOnTrack(phase.loserWinnerHouse, cur._1)
        )
    )
  }
}
