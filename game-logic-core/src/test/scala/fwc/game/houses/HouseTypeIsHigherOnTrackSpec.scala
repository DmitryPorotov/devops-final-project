package fwc.game.houses

import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class HouseTypeIsHigherOnTrackSpec extends AnyFlatSpec with should.Matchers {
  "isHigherOnTrack" should "compare on given track" in {
    val mooseIsHigherThen = HouseMoose.isHigherOnTrack(Seq(HouseMoose, HouseKraken, HouseWolf))
    assert(mooseIsHigherThen(HouseWolf))

    extension (ht: HouseType)
      def isHigher(ht2: HouseType): Boolean =
        ht.isHigherOnTrack(Seq(HouseMoose, HouseKraken, HouseWolf))(ht2)


    val wolfIsHigherThen = HouseWolf.isHigherOnTrack(Seq(HouseMoose, HouseKraken, HouseWolf))
    assert(!wolfIsHigherThen(HouseMoose))

    assert(HouseMoose isHigher HouseWolf)
  }
}
