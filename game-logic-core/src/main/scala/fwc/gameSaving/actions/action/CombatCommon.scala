package fwc.gameSaving.actions.action

import fwc.game.actionPhase.DiscardedHouseCards
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitSiegeEngines, TileNumber}
import fwc.game.houses.*
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseChooseHouseCard, SubPhaseResolveHouseCard, SubPhaseResolveSupportOrder}
import fwc.game.planningPhase.{Order, OrderMarch}
import fwc.gameLoading.HouseCard

object CombatCommon {
  //note: move to Combat object?
  @throws[AttackNeutralException]
  def getNewSubPhaseForMarchSupport(
                                     supportOrdersForTile: Map[TileNumber, (HouseType, Order)],
                                     sortedHouses: Seq[HouseType],
                                     attackingHouse: HouseType,
                                     defendingHouse: HouseType,
                                   ): SubPhase = {
    val supportOrders = supportOrdersForTile
      .foldLeft[(Set[Int], Set[HouseType])]((Set(), Set()))(
        (acc, tileNumberHouseOrder) =>
          (acc._1 + tileNumberHouseOrder._1, acc._2 + tileNumberHouseOrder._2._1)
      )

    if supportOrders._1.nonEmpty
    then SubPhaseResolveSupportOrder(
      supportOrders._2.toSeq
        .sortWith((a, b) => sortedHouses.indexOf(a) < sortedHouses.indexOf(b))
        .head,
      supportOrders._1.toSeq
    )
    else if defendingHouse != HouseNeutral
    then SubPhaseChooseHouseCard(Seq(attackingHouse, defendingHouse))
    else throw new AttackNeutralException
  }

  def getImmediatelyResolvableCardSubPhase(
                                            houseCard: HouseCard,
                                            krakenPowerTokens: Int,
                                          ): SubPhaseResolveHouseCard = {
    houseCard match
      case HouseCard(HousePufferfish, 0, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HousePufferfish, 0)
      case HouseCard(HouseKraken, 6, _, _, _, _, _)
      => if krakenPowerTokens > 1
          then SubPhaseResolveHouseCard(HouseKraken, 6)
          else null
      case HouseCard(HouseRose, 2, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseRose, 2)
      case HouseCard(HouseRose, 4, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseRose, 4)
      case HouseCard(HouseLion, 5, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseLion, 5)
      case _ => null
  }

  def attackNeutrals(
                      gameState: GameState
                    ): GameState = {
    val tileNumberUnderAttack = gameState.combat.defenderTileNum
    val attackerArmy = gameState.combat.attackerArmy
    val sumUnitStrength = (acc: Int, mu: MilitaryUnit) =>
      if mu.unitType == MilitaryUnitSiegeEngines
        && gameRules.board(tileNumberUnderAttack).musteringPoints == 0
      then acc
      else acc + mu.unitType.strength

    val attackingArmyStr = attackerArmy.foldLeft(0)(sumUnitStrength)
      + gameState.combat.attackerSupport.foldLeft(0)(
      (acc, cur) =>
        acc + gameState.armies(cur).foldLeft(0)(sumUnitStrength)
    )

    val newPhase = NextOrderFinder.nextSubPhase(gameState, OrderMarch, gameState.combat.attackerHouse)
    if attackingArmyStr >= gameState.combat.defenderArmy.head.garrisonDefensePoints
    then
      val armiesWithoutNeutralGarrison = gameState.armies - tileNumberUnderAttack
      gameState.copy(
        subPhase = newPhase,
        armies =
          armiesWithoutNeutralGarrison + (tileNumberUnderAttack -> attackerArmy),
        combat = null
      )
    else
      val remainingArmiesAtSourceTileAfterDefeat =
        gameState.armies.getOrElse(tileNumberUnderAttack, Seq())
          :++ attackerArmy.map(_.copy(isDefeated = true))
      gameState.copy(
        subPhase = newPhase,
        armies = gameState.armies + (gameState.combat.attackerTileNum -> remainingArmiesAtSourceTileAfterDefeat),
        combat = null
      )
  }
}
