package fwc.game.eventsPhase


import fwc.game.houses.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class BidsSpec extends AnyFlatSpec with should.Matchers {
  "Bids" should "be able to validate a tie resolution" in {
    val bids = Bids(
      Map(
        HouseMoose -> 5,
        HouseWolf -> 3,
        HouseLion -> 4,
        HouseRose -> 4,
        HousePufferfish -> 3,
        HouseKraken -> 6
      )
    )

    val validResolution = Seq(
      HouseKraken, HouseMoose, HouseLion, HouseRose, HouseWolf, HousePufferfish
    )

    assert(bids.validateTieResolution(validResolution))

    val invalidResolution = Seq(
      HouseRose, HouseKraken, HouseMoose, HouseLion, HouseRose, HouseWolf, HousePufferfish
    )

    assert(!bids.validateTieResolution(invalidResolution))
  }

  "Bids" should "be able to find winner or loser candidates" in {
    val bids = Bids(
      Map(
        HouseMoose -> 5,
        HouseWolf -> 3,
        HouseLion -> 4,
        HouseRose -> 4,
        HousePufferfish -> 3,
        HouseKraken -> 5
      )
    )

    val c = bids.getLoserOrWinnerCandidatesInWildlingsBids(12)
    assert(c._2.contains(HouseMoose))
    assert(c._2.contains(HouseKraken))
    c._2.size === 2
    assert(c._1)

    val bids2 = Bids(
      Map(
        HouseMoose -> 0,
        HouseWolf -> 3,
        HouseLion -> 4,
        HouseRose -> 2,
        HousePufferfish -> 1,
        HouseKraken -> 0
      )
    )

    val c2 = bids2.getLoserOrWinnerCandidatesInWildlingsBids(12)
    assert(c2._2.contains(HouseMoose))
    assert(c2._2.contains(HouseKraken))
    c2._2.size === 2
    assert(!c2._1)
  }

}
