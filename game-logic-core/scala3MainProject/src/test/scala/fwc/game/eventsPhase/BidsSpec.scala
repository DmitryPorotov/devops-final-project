package fwc.game.eventsPhase


import fwc.game.houses.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class BidsSpec extends AnyFlatSpec with should.Matchers {
  "Bids" should "be able to validate a tie resolution" in {
    val bids = Bids(
      Map(
        HouseType.Moose -> 5,
        HouseType.Wolf -> 3,
        HouseType.Lion -> 4,
        HouseType.Rose -> 4,
        HouseType.PufferFish -> 3,
        HouseType.Kraken -> 6
      )
    )

    val validResolution = Seq(
      HouseType.Kraken, 
      HouseType.Moose, 
      HouseType.Lion, 
      HouseType.Rose, 
      HouseType.Wolf, 
      HouseType.PufferFish
    )

    assert(bids.validateTieResolution(validResolution))

    val invalidResolution = Seq(
      HouseType.Rose, 
      HouseType.Kraken, 
      HouseType.Moose, 
      HouseType.Lion, 
      HouseType.Rose, 
      HouseType.Wolf, 
      HouseType.PufferFish
    )

    assert(!bids.validateTieResolution(invalidResolution))
  }

  "Bids" should "be able to find winner or loser candidates" in {
    val bids = Bids(
      Map(
        HouseType.Moose -> 5,
        HouseType.Wolf -> 3,
        HouseType.Lion -> 4,
        HouseType.Rose -> 4,
        HouseType.PufferFish -> 3,
        HouseType.Kraken -> 5
      )
    )

    val c = bids.getLoserOrWinnerCandidatesInWildlingsBids(12)
    assert(c._2.contains(HouseType.Moose))
    assert(c._2.contains(HouseType.Kraken))
    c._2.size === 2
    assert(c._1)

    val bids2 = Bids(
      Map(
        HouseType.Moose -> 0,
        HouseType.Wolf -> 3,
        HouseType.Lion -> 4,
        HouseType.Rose -> 2,
        HouseType.PufferFish -> 1,
        HouseType.Kraken -> 0
      )
    )

    val c2 = bids2.getLoserOrWinnerCandidatesInWildlingsBids(12)
    assert(c2._2.contains(HouseType.Moose))
    assert(c2._2.contains(HouseType.Kraken))
    c2._2.size === 2
    assert(!c2._1)
  }

}
