package fwc.game.board

import fwc.game.board
import fwc.game.houses.*
import fwc.gameLoading
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class TracksSpec extends AnyFlatSpec with should.Matchers {

  "Track object" should "initialize the tracks" in {
    val boardStart = gameLoading.loadBoardStart()

    val tracks = Tracks.initialize(boardStart)

    assert(tracks.isInstanceOf[Tracks])

    assert(tracks.throneOwner == HouseType.Moose, "Moose should be on the iron throne")
    assert(tracks.steelBladeOwner == HouseType.Kraken, "Kraken should be first on fiefdoms track")
    assert(tracks.ravenOwner == HouseType.Lion, "Lion should be first on court track")
  }

  "Tracks" should "be able to reduce 2 positions" in {
    val boardStart = gameLoading.loadBoardStart()

    val tracks = Tracks.initialize(boardStart)

    val t2 = tracks.reduce2PositionsOnTrack(TrackType.Throne, HouseType.Wolf)

    assert(t2(TrackType.Throne).indexOf(HouseType.Wolf) == 4)

    val t3 = tracks.reduce2PositionsOnTrack(TrackType.Throne, HouseType.Moose)

    assert(t3(TrackType.Throne).indexOf(HouseType.Moose) == 2)

    val t4 = tracks.reduce2PositionsOnTrack(TrackType.Throne, HouseType.Rose)

    assert(t4(TrackType.Throne).indexOf(HouseType.Rose) == 5)
  }

}
