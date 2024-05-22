package fwc.game.eventsPhase.taxesSpec

import fwc.game.*
import fwc.game.board.*
import fwc.game.houses.*
import fwc.game.eventsPhase.Taxes
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import org.scalatest.funspec.AnyFunSpec

class CollectTaxesSpec extends AnyFlatSpec with should.Matchers {

  "Taxes object collectTaxes function" should "be able to collect taxes" in {
    val gameState = fwc.game.initializeGameState()

    val powerTokens = Taxes.collectTaxes(gameState.armies, gameState.powerTokens)

    val neededResult = Map[HouseType, Int](
      HouseWolf -> 6,
      HouseKraken -> 7,
      HouseRose -> 6,
      HouseMoose -> 7,
      HousePufferfish -> 6,
      HouseLion -> 6
    )

    powerTokens.tokens.foreach((h , n) => {
      n === neededResult.getOrElse(h, 0)
    })
  }
}
