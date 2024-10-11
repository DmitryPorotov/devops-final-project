package fwc.game.actions.action

import fwc.game.actionPhase.{CombatOutcome, DiscardedHouseCards}
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.houses.*
import fwc.game.houses.HouseType
import fwc.game.phases.{SubPhase, SubPhaseSingleHouse}
import fwc.game.phases.actionSubPhases.{SubPhaseChooseHouseCard, SubPhaseCleanUpAfterCombat, SubPhaseResolveCardRose2, SubPhaseResolveHouseCard, SubPhaseResolveSupportOrder}
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
    then {
      val supOrdersOfHouse = supportOrders._2.toSeq
        .sortWith((a, b) => sortedHouses.indexOf(a) < sortedHouses.indexOf(b))
        .head
      SubPhaseResolveSupportOrder(
        supOrdersOfHouse,
        supportOrdersForTile.filter(x => x._2._1 == supOrdersOfHouse).foldLeft(Seq[Int]())((acc, cur) => {
          acc :+ cur._1
        })
      )
    }
    else if defendingHouse != HouseType.Neutral
    then SubPhaseChooseHouseCard(Seq(attackingHouse, defendingHouse))
    else throw new AttackNeutralException
  }

  def getImmediatelyResolvableCardSubPhase(
                                            houseCard: HouseCard,
                                            krakenPowerTokens: Int,
                                          ): SubPhaseSingleHouse = {
    houseCard match
      case HouseCard(HouseType.PufferFish, 0, _, _, _, _, _)
      => SubPhaseResolveHouseCard(HouseType.PufferFish, 0)
      case HouseCard(HouseType.Kraken, 6, _, _, _, _, _)
      => if krakenPowerTokens > 1
          then SubPhaseResolveHouseCard(HouseType.Kraken, 6)
          else null
      case HouseCard(HouseType.Rose, 2, _, _, _, _, _)
      => SubPhaseResolveCardRose2(HouseType.Rose)
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
    ) + gameState.combat.attackerOrder.modifier

    val newPhase = SubPhaseCleanUpAfterCombat(Seq(gameState.combat.attackerHouse, HouseType.Neutral))
    val isAttackersWin = attackingArmyStr >= gameState.combat.defenderArmy.head.garrisonDefensePoints
    val updatedCombat = gameState.combat.copy(
      combatOutcome = CombatOutcome(
        attackingArmyStr,
        gameState.combat.defenderArmy.head.garrisonDefensePoints,
        if isAttackersWin then Some(gameState.combat.attackerHouse) else Some(HouseType.Neutral),
        0,
        0
      )
    )
    if isAttackersWin
    then
      val armiesWithoutNeutralGarrison = gameState.armies - tileNumberUnderAttack
      gameState.copy(
        subPhase = newPhase,
        armies =
          armiesWithoutNeutralGarrison,
        combat = updatedCombat
      )
    else
      val remainingArmiesAtSourceTileAfterDefeat =
        gameState.armies.getOrElse(gameState.combat.attackerTileNum, Seq())
          :++ attackerArmy.map(_.copy(isDefeated = true))
      val updatedCombat2 = updatedCombat.copy(
        attackerArmy = Seq()
      )
      gameState.copy(
        subPhase = newPhase,
        armies = gameState.armies + (gameState.combat.attackerTileNum -> remainingArmiesAtSourceTileAfterDefeat),
        combat = updatedCombat2
      )
  }
}
