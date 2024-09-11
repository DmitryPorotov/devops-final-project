package fwc.game.actionPhase

import fwc.game.{GameState, gameRules}
import fwc.game.board.*
import fwc.game.houses.*
import fwc.game.planningPhase.{Order, OrderType}
import fwc.gameLoading.{HouseCard, TidesOfBattleCard}

// todo write unit tests for this class
class CombatOutcomeCalculator(gameState: GameState) {
  extension (ht1: HouseType)
    def isHigherOnThroneTrackThan(ht2: HouseType): Boolean =
      ht1.isHigherOnTrack(gameState.tracks(TrackType.Throne))(ht2)

    private def isHigherOnFiefdomsTrackThan(ht2: HouseType): Boolean =
      ht1.isHigherOnTrack(gameState.tracks(TrackType.Fiefdoms))(ht2)

  def calculate(): CombatOutcome = {
    val attackerStrFunc = (acc: Int, mu: MilitaryUnit) =>
      if mu.isDefeated
      then 0
      else
        mu.unitType match
        case MilitaryUnitType.Ships =>
            attackerShipStrength(gameState.combat.attackerCard, gameState.combat.defenderCard, mu)
            + acc
        case MilitaryUnitType.Footmen =>
          val card = gameState.combat.attackerCard
          (if card.isLion4
          then 2
          else 1)
            + acc
        case MilitaryUnitType.Knights => MilitaryUnitType.Knights.strength + acc
        case MilitaryUnitType.SiegeEngines =>
          acc +
            (if gameRules.board(gameState.combat.defenderTileNum).musteringPoints > 0
            then MilitaryUnitType.SiegeEngines.strength
            else 0)
        case MilitaryUnitType.Garrison => acc
        case MilitaryUnitType.PowerToken => acc

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
          case MilitaryUnitType.Ships =>
            defenderShipStrength(gameState.combat.attackerCard, gameState.combat.defenderCard, mu)
              + acc
          case MilitaryUnitType.Footmen => MilitaryUnitType.Footmen.strength + acc
          case MilitaryUnitType.Knights => MilitaryUnitType.Knights.strength + acc
          case MilitaryUnitType.SiegeEngines => acc
          case MilitaryUnitType.Garrison => mu.garrisonDefensePoints + acc
          case MilitaryUnitType.PowerToken => acc

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

  private def attackerShipStrength(attackerCard: HouseCard, defenderCard: HouseCard, mu: MilitaryUnit): Int = {
    if mu.unitType != MilitaryUnitType.Ships
    then throw new RuntimeException("Only for ships")

    if attackerCard != null
      && attackerCard.isKraken4
      && defenderCard != null
      && defenderCard.isMoose6
      && gameState.combat.defenderSupport.nonEmpty
    then
      if HouseType.Kraken isHigherOnThroneTrackThan HouseType.Moose
      then
        if mu.house == HouseType.Kraken
        then 2
        else 0
      else 0
    else
      if attackerCard != null
        && attackerCard.isKraken4
        && !(defenderCard != null && defenderCard.isMoose6)
      then
        if mu.house == HouseType.Kraken
        then 2
        else 1
      else
        if oneOfCardsIsMoose6(attackerCard, defenderCard)
        then
          if mu.house != HouseType.Moose
          then 0
          else 1
        else 1
  }

  private def oneOfCardsIsMoose6(attackerCard: HouseCard, defenderCard: HouseCard): Boolean =
    (attackerCard != null && attackerCard.isMoose6 && gameState.combat.attackerSupport.nonEmpty)
      || (defenderCard != null && defenderCard.isMoose6 && gameState.combat.defenderSupport.nonEmpty)

  private def defenderShipStrength(attackerCard: HouseCard, defenderCard: HouseCard, mu: MilitaryUnit): Int = {
    if mu.unitType != MilitaryUnitType.Ships
    then throw new RuntimeException("Only for ships")

    if oneOfCardsIsMoose6(attackerCard, defenderCard)
    then
      if mu.house != HouseType.Moose
      then 0
      else 1
    else 1
  }

  private def getDefenderOrderStrength(houseCard: HouseCard, order: Order): Int = {
    if order == null || order.orderType != OrderType.Defend
    then 0
    else
      if houseCard != null && houseCard.isWolf6
      then order.modifier * 2
      else order.modifier
  }

  private def countKills(combatOutcome: CombatOutcome, winner: HouseType): CombatOutcome = {
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

  private def getHouseCardAttack(houseCard: HouseCard): Int = {
    val bonus =
      houseCard match
        case HouseCard(HouseType.Moose, 5, _, _, _, _, _) =>
          if gameState.discardedHouseCards.getOrElse(HouseType.Moose, Seq()).contains(0)
          then 1
          else 0
        case HouseCard(HouseType.Kraken, 1, _, _, _, _, _) =>
          if gameState.combat.defenderHouse == HouseType.Kraken
            && gameRules.board(gameState.combat.defenderTileNum).musteringPoints > 0
          then 1
          else 0
        case HouseCard(HouseType.PufferFish, 6, _, _, _, _, _) =>
          if gameState.combat.attackerHouse == HouseType.PufferFish
          then 1
          else 0
        case HouseCard(HouseType.Kraken, 5, _, _, _, _, _) =>
          if (gameState.combat.attackerHouse == HouseType.Kraken
            && gameState.combat.attackerSupport.isEmpty)
            ||
            (gameState.combat.defenderHouse == HouseType.Kraken
              && gameState.combat.defenderSupport.isEmpty)
          then 2
          else 0
        case _ => 0
    bonus + houseCard.attack
  }

  private def getHouseCardDefence(houseCard: HouseCard): Int = {
    val bonus =
      houseCard match
        case HouseCard(HouseType.PufferFish, 6, _, _, _, _, _) =>
          if gameState.combat.defenderHouse == HouseType.PufferFish
          then 1
          else 0
        case HouseCard(HouseType.Kraken, 5, _, _, _, _, _) =>
          if (gameState.combat.attackerHouse == HouseType.Kraken
            && gameState.combat.attackerSupport.isEmpty)
            ||
            (gameState.combat.defenderHouse == HouseType.Kraken
              && gameState.combat.defenderSupport.isEmpty)
          then 1
          else 0
        case _ => 0
    bonus + houseCard.defense
  }

  private def getCardStrength(houseCard: HouseCard, opponentHouseCard: HouseCard): Int = {
    val str = if opponentHouseCard.house == HouseType.Kraken && opponentHouseCard.code == 3
      then 0
    else houseCard.strength
    str + getCardBonusStrength(houseCard)
  }

  private def getCardBonusStrength(houseCard: HouseCard): Int = {
    houseCard match
      case HouseCard(HouseType.Wolf, 6, _, _, _, _, _) =>
        val order = gameState.placedOrders.getOrderByTileNumber(gameState.combat.defenderTileNum)
        if gameState.combat.defenderHouse == HouseType.Wolf
          && order.nonEmpty
          && order.head._1 == HouseType.Wolf
          && order.head._2.orderType == OrderType.Defend
        then order.head._2.modifier
        else 0
      case HouseCard(HouseType.Moose, 0, _, _, _, _, _) =>
        val opponentHouse =
          if gameState.combat.attackerHouse == HouseType.Moose
          then gameState.combat.defenderHouse
          else gameState.combat.attackerHouse
        if opponentHouse isHigherOnThroneTrackThan HouseType.Moose
        then 1
        else 0
      case HouseCard(HouseType.Moose, 5, _, _, _, _, _) =>
        if gameState.discardedHouseCards.getOrElse(HouseType.Moose, Seq()).contains(0)
        then 1
        else 0
      case HouseCard(HouseType.Kraken, 1, _, _, _, _, _) =>
        if gameState.combat.defenderHouse == HouseType.Kraken
          && gameRules.board(gameState.combat.defenderTileNum).musteringPoints > 0
        then 1
        else 0

      case _ => 0
  }

}
