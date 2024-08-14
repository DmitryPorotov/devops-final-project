package fwc.gameSaving.actions.roundEvents.wildlingsCards

import fwc.game.actionPhase.DiscardedHouseCards
import fwc.game.houses.HouseType
import fwc.game.phases.roundEventsSubPhases.SubPhaseWildlingsCard
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class Card2Spec extends AnyFlatSpec with should.Matchers {
  private val gameState = fwc.game.initializeGameState().copy(
    subPhase = SubPhaseWildlingsCard(
      HouseType.getSeqOfAll,
      HouseType.Moose,
      2,
      false
    ),
    wildlingsStartedFrom12Points = Some(true)
  )

  "Card2" should "be able to get loser cards" in {
    val card2 = Card2(gameState)

    val cards = card2.getLoserCards(HouseType.Moose, Seq(1,2))

    assert(!cards.exists(_.code == 1))
    assert(!cards.exists(_.code == 2))
    assert(cards.size === 5)
  }

  "Card2" should "be able to get max strength of cards left" in {
    val card2 = Card2(gameState)

    val cards = card2.getLoserCards(HouseType.Moose, Seq(0, 2))

    val maxStr = card2.getMaxStrength(cards)

    assert(maxStr == 2)
  }

  "Card2" should "be able to find houses who need to discard a card" in {
    val card2 = Card2(gameState)

    val cards = card2.getHousesWhoNeedToDiscardACard(
      HouseType.getSeqOfAll,
      HouseType.Moose,
      DiscardedHouseCards(
        Map(
          HouseType.Wolf -> Seq(0,1,2,3,4,5)
        )
      )
    )

    assert(cards.size == 4)
    assert(!cards.contains(HouseType.Wolf))
    assert(!cards.contains(HouseType.Moose))
  }


}
