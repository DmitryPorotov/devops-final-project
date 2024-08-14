package fwc.game.houses

import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class HouseTypeIsHigherOnTrackSpec extends AnyFlatSpec with should.Matchers {
  "isHigherOnTrack" should "compare on given track" in {
    val mooseIsHigherThen = HouseType.Moose.isHigherOnTrack(Seq(HouseType.Moose, HouseType.Kraken, HouseType.Wolf))
    assert(mooseIsHigherThen(HouseType.Wolf))

    extension (ht: HouseType)
      def isHigher(ht2: HouseType): Boolean =
        ht.isHigherOnTrack(Seq(HouseType.Moose, HouseType.Kraken, HouseType.Wolf))(ht2)


    val wolfIsHigherThen = HouseType.Wolf.isHigherOnTrack(Seq(HouseType.Moose, HouseType.Kraken, HouseType.Wolf))
    assert(!wolfIsHigherThen(HouseType.Moose))

    assert(HouseType.Moose isHigher HouseType.Wolf)
  }
}
