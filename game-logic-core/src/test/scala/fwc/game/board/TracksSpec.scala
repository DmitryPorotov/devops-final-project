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

    assert(tracks.throneOwner == HouseMoose, "Moose should be on the iron throne")
    assert(tracks.steelBladeOwner == HouseKraken, "Kraken should be first on fiefdoms track")
    assert(tracks.ravenOwner == HouseLion, "Lion should be first on court track")
  }

  "Tracks" should "be able to reduce 2 positions" in {
    val boardStart = gameLoading.loadBoardStart()

    val tracks = Tracks.initialize(boardStart)

    val t2 = tracks.reduce2PositionsOnTrack(TrackThrone, HouseWolf)

    assert(t2(TrackThrone).indexOf(HouseWolf) == 4)

    val t3 = tracks.reduce2PositionsOnTrack(TrackThrone, HouseMoose)

    assert(t3(TrackThrone).indexOf(HouseMoose) == 2)

    val t4 = tracks.reduce2PositionsOnTrack(TrackThrone, HouseRose)

    assert(t4(TrackThrone).indexOf(HouseRose) == 5)
  }

}
