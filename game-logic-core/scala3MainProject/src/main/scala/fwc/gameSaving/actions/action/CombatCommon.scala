package fwc.gameSaving.actions.action

import fwc.game.actionPhase.DiscardedHouseCards
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.houses.*
import fwc.game.phases.SubPhase
import fwc.game.phases.actionSubPhases.{SubPhaseChooseHouseCard, SubPhaseResolveHouseCard, SubPhaseResolveSupportOrder}
import fwc.game.planningPhase.{Order, OrderType}
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
    else if defendingHouse != HouseType.Neutral
    then SubPhaseChooseHouseCard(Seq(attackingHouse, defendingHouse))
    else throw new AttackNeutralException
  }

  def getImmediatelyResolvableCardSubPhase(
                                            houseCard: HouseCard,
                                            krakenPowerTokens: Int,
                                          ): SubPhaseResolveHouseCard = {
    houseCard match
      case HouseCard(HouseType.PufferFish, 0, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseType.PufferFish, 0)
      case HouseCard(HouseType.Kraken, 6, _, _, _, _, _)
      => if krakenPowerTokens > 1
          then SubPhaseResolveHouseCard(HouseType.Kraken, 6)
          else null
      case HouseCard(HouseType.Rose, 2, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseType.Rose, 2)
      case HouseCard(HouseType.Rose, 4, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseType.Rose, 4)
      case HouseCard(HouseType.Lion, 5, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseType.Lion, 5)
      case _ => null
  }

  def attackNeutrals(
                      gameState: GameState
                    ): GameState = {
    val tileNumberUnderAttack = gameState.combat.defenderTileNum
    val attackerArmy = gameState.combat.attackerArmy
    val sumUnitStrength = (acc: Int, mu: MilitaryUnit) =>
      if mu.unitType == MilitaryUnitType.SiegeEngines
        && gameRules.board(tileNumberUnderAttack).musteringPoints == 0
      then acc
      else acc + mu.unitType.strength

    val attackingArmyStr = attackerArmy.foldLeft(0)(sumUnitStrength)
      + gameState.combat.attackerSupport.foldLeft(0)(
      (acc, cur) =>
        acc + gameState.armies(cur).foldLeft(0)(sumUnitStrength)
    )

    val newPhase = NextOrderFinder.nextSubPhase(gameState, OrderType.OrderMarch, gameState.combat.attackerHouse)
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
