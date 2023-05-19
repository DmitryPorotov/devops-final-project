package fwc.game.actionPhase

import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitFootmen, MilitaryUnitGarrison, MilitaryUnitKnights, MilitaryUnitPowerToken, MilitaryUnitShips, MilitaryUnitSiegeEngines, MilitaryUnitType, TileNumber, TrackFiefdoms, TrackThrone}
import fwc.game.houses.*
import fwc.game.planningPhase.{Order, OrderDefend}
import fwc.gameLoading.{HouseCard, TidesOfBattleCard}

class CombatOutcomeCalculator(gameState: GameState) {
  extension (ht1: HouseType)
    def isHigherOnThroneTrackThan(ht2: HouseType): Boolean =
      ht1.isHigherOnTrack(gameState.tracks(TrackThrone))(ht2)

    def isHigherOnFiefdomsTrackThan(ht2: HouseType): Boolean =
      ht1.isHigherOnTrack(gameState.tracks(TrackFiefdoms))(ht2)

  def calculate(): CombatOutcome = {
    val attackerStrFunc = (acc: Int, mu: MilitaryUnit) =>
      if mu.isDefeated
      then 0
      else
        mu.unitType match
        case MilitaryUnitShips =>
            attackerShipStrength(gameState.combat.attackerCard, gameState.combat.defenderCard, mu)
            + acc
        case MilitaryUnitFootmen =>
          val card = gameState.combat.attackerCard
          (if card.code == 4 && card.house == HouseLion
          then 2
          else 1)
            + acc
        case MilitaryUnitKnights => MilitaryUnitKnights.strength + acc
        case MilitaryUnitSiegeEngines =>
          acc +
            (if gameRules.board(gameState.combat.defenderTileNum).musteringPoints > 0
            then MilitaryUnitSiegeEngines.strength
            else 0)
        case MilitaryUnitGarrison => acc
        case MilitaryUnitPowerToken => acc

    val attackerArmyStrength = gameState.combat.attackerArmy.foldLeft(0)(
      attackerStrFunc
    )
      + gameState.combat.attackerSupport.foldLeft(0)(
      (acc: Int, tn: TileNumber) =>
        gameState.armies(tn).foldLeft(0)(attackerStrFunc) + acc
    )

    val defenderStrFunc = (acc: Int, mu: MilitaryUnit) =>
      if mu.isDefeated
      then 0
      else
        mu.unitType match
          case MilitaryUnitShips =>
            defenderShipStrength(gameState.combat.attackerCard, gameState.combat.defenderCard, mu)
              + acc
          case MilitaryUnitFootmen => MilitaryUnitKnights.strength + acc
          case MilitaryUnitKnights => MilitaryUnitKnights.strength + acc
          case MilitaryUnitSiegeEngines => acc
          case MilitaryUnitGarrison => mu.garrisonDefensePoints + acc
          case MilitaryUnitPowerToken => acc

    val defenderArmyStrength = gameState.combat.defenderArmy.foldLeft(0)(
      defenderStrFunc
    )
      + gameState.combat.defenderSupport.foldLeft(0)(
      (acc: Int, tn: TileNumber) =>
        gameState.armies(tn).foldLeft(0)(defenderStrFunc) + acc
    )

    val attackerCardStr = getCardStrength(gameState.combat.attackerCard, gameState.combat.defenderCard)
    val defenderCardStr = getCardStrength(gameState.combat.defenderCard, gameState.combat.attackerCard)

    val existingOutcome = gameState.combat.combatOutcome

    val outcome1 = CombatOutcome(
      (if existingOutcome != null
        then existingOutcome.attackerStrength
        else 0) +
        attackerArmyStrength
        + attackerCardStr
        + gameState.combat.attackerTidesOfBattle.power
        + gameState.combat.attackerOrder.modifier,
      (if existingOutcome != null
      then existingOutcome.defenderStrength
      else 0) +
      defenderArmyStrength
        + defenderCardStr
        + gameState.combat.defenderTidesOfBattle.power
        + getDefenderOrderStrength(gameState.combat.defenderCard, gameState.combat.defenderOrder)
      ,
      None,
      0,
      0
    )

    val winner =
      if outcome1.attackerStrength == outcome1.defenderStrength
      then
        if gameState.combat.attackerHouse isHigherOnFiefdomsTrackThan gameState.combat.defenderHouse
        then gameState.combat.attackerHouse
        else gameState.combat.defenderHouse
      else
        if outcome1.attackerStrength > outcome1.defenderStrength
        then gameState.combat.attackerHouse
        else gameState.combat.defenderHouse

    countKills(outcome1, winner)
  }

  def attackerShipStrength(attackerCard: HouseCard, defenderCard: HouseCard, mu: MilitaryUnit): Int = {
    if mu.unitType != MilitaryUnitShips
    then throw new RuntimeException("Only for ships")

    if attackerCard != null
      && attackerCard.isKraken4
      && defenderCard != null
      && defenderCard.isMoose6
      && gameState.combat.defenderSupport.nonEmpty
    then
      if HouseKraken isHigherOnThroneTrackThan HouseMoose
      then
        if mu.house == HouseKraken
        then 2
        else 0
      else 0
    else
      if attackerCard != null
        && attackerCard.isKraken4
        && !(defenderCard != null && defenderCard.isMoose6)
      then
        if mu.house == HouseKraken
        then 2
        else 1
      else
        if oneOfCardsIsMoose6(attackerCard, defenderCard)
        then
          if mu.house != HouseMoose
          then 0
          else 1
        else 1
  }

  private def oneOfCardsIsMoose6(attackerCard: HouseCard, defenderCard: HouseCard): Boolean =
    (attackerCard != null && attackerCard.isMoose6 && gameState.combat.attackerSupport.nonEmpty)
      || (defenderCard != null && defenderCard.isMoose6 && gameState.combat.defenderSupport.nonEmpty)

  def defenderShipStrength(attackerCard: HouseCard, defenderCard: HouseCard, mu: MilitaryUnit): Int = {
    if mu.unitType != MilitaryUnitShips
    then throw new RuntimeException("Only for ships")

    if oneOfCardsIsMoose6(attackerCard, defenderCard)
    then
      if mu.house != HouseMoose
      then 0
      else 1
    else 1
  }

  def getDefenderOrderStrength(houseCard: HouseCard, order: Order): Int = {
    if order == null || order.orderType != OrderDefend
    then 0
    else
      if houseCard != null && houseCard.isWolf6
      then order.modifier * 2
      else order.modifier
  }

  def countKills(combatOutcome: CombatOutcome, winner: HouseType): CombatOutcome = {
    val combat = gameState.combat
    def countDeaths(winnerHC: HouseCard,
                    winnerTOBc: TidesOfBattleCard,
                    loserHC:HouseCard,
                    loserTOBc: TidesOfBattleCard): Int = {
      val normalCasualties =
        (if winnerHC != null
        then getHouseCardAttack(winnerHC)
        else 0)
          +
          (if winnerTOBc.attack then 1 else 0)
          -
          (if loserHC != null
          then getHouseCardDefence(loserHC)
          else 0)
          -
          (if loserTOBc.defense then 1 else 0)

      (if normalCasualties < 0
      then 0
      else normalCasualties)
        + (if winnerTOBc.death then 1 else 0)
    }

    def isWolf5(houseCard: HouseCard): Boolean = {
      houseCard != null && houseCard.isWolf5
    }

    val defenderDeaths =
      if isWolf5(combat.defenderCard)
      then 0
      else
        if winner == combat.attackerHouse
        then
          countDeaths(combat.attackerCard, combat.attackerTidesOfBattle, combat.defenderCard, combat.defenderTidesOfBattle)
        else
          (if combat.attackerTidesOfBattle.death then 1 else 0)

    val attackerDeaths =
      if isWolf5(combat.attackerCard)
      then 0
      else
        if winner == combat.attackerHouse
        then (if combat.defenderTidesOfBattle.death then 1 else 0)
        else countDeaths(combat.defenderCard, combat.defenderTidesOfBattle, combat.attackerCard, combat.attackerTidesOfBattle)

    combatOutcome.copy(
      winner = Some(winner),
      attackerUnitsToKill = attackerDeaths,
      defenderUnitsToKill = defenderDeaths
    )
  }

  def getHouseCardAttack(houseCard: HouseCard): Int = {
    val bonus =
      houseCard match
        case HouseCard(HouseMoose, 5, _, _, _, _, _) =>
          if gameState.discardedHouseCards.getOrElse(HouseMoose, Seq()).contains(0)
          then 1
          else 0
        case HouseCard(HouseKraken, 1, _, _, _, _, _) =>
          if gameState.combat.defenderHouse == HouseKraken
            && gameRules.board(gameState.combat.defenderTileNum).musteringPoints > 0
          then 1
          else 0
        case HouseCard(HousePufferfish, 6, _, _, _, _, _) =>
          if gameState.combat.attackerHouse == HousePufferfish
          then 1
          else 0
        case HouseCard(HouseKraken, 5, _, _, _, _, _) =>
          if (gameState.combat.attackerHouse == HouseKraken
            && gameState.combat.attackerSupport.isEmpty)
            ||
            (gameState.combat.defenderHouse == HouseKraken
              && gameState.combat.defenderSupport.isEmpty)
          then 2
          else 0
        case _ => 0
    bonus + houseCard.attack
  }

  def getHouseCardDefence(houseCard: HouseCard): Int = {
    val bonus =
      houseCard match
        case HouseCard(HousePufferfish, 6, _, _, _, _, _) =>
          if gameState.combat.defenderHouse == HousePufferfish
          then 1
          else 0
        case HouseCard(HouseKraken, 5, _, _, _, _, _) =>
          if (gameState.combat.attackerHouse == HouseKraken
            && gameState.combat.attackerSupport.isEmpty)
            ||
            (gameState.combat.defenderHouse == HouseKraken
              && gameState.combat.defenderSupport.isEmpty)
          then 1
          else 0
        case _ => 0
    bonus + houseCard.defense
  }

  def getCardStrength(houseCard: HouseCard, opponentHouseCard: HouseCard): Int = {
    val str = if opponentHouseCard.house == HouseKraken && opponentHouseCard.code == 3
      then 0
    else houseCard.strength
    str + getCardBonusStrength(houseCard)
  }

  def getCardBonusStrength(houseCard: HouseCard): Int = {
    houseCard match
      case HouseCard(HouseWolf, 6, _, _, _, _, _) =>
        val order = gameState.placedOrders.getOrderByTileNumber(gameState.combat.defenderTileNum)
        if gameState.combat.defenderHouse == HouseWolf
          && order.nonEmpty
          && order.head._1 == HouseWolf
          && order.head._2.orderType == OrderDefend
        then order.head._2.modifier
        else 0
      case HouseCard(HouseMoose, 0, _, _, _, _, _) =>
        val opponentHouse =
          if gameState.combat.attackerHouse == HouseMoose
          then gameState.combat.defenderHouse
          else gameState.combat.attackerHouse
        if opponentHouse isHigherOnThroneTrackThan HouseMoose
        then 1
        else 0
      case HouseCard(HouseMoose, 5, _, _, _, _, _) =>
        if gameState.discardedHouseCards.getOrElse(HouseMoose, Seq()).contains(0)
        then 1
        else 0
      case HouseCard(HouseKraken, 1, _, _, _, _, _) =>
        if gameState.combat.defenderHouse == HouseKraken
          && gameRules.board(gameState.combat.defenderTileNum).musteringPoints > 0
        then 1
        else 0

      case _ => 0
  }

}
