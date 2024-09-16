package fwc.game.actions.action

import fwc.JsonSerializable
import fwc.game.actionPhase.{Combat, DiscardedHouseCards}
import fwc.game.actions.{Action, JsonParsableAction}
import fwc.game.{GameState, gameRules}
import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitType, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.phases.actionSubPhases.{SubPhaseCalculateGameWinner, SubPhaseResolveHouseCard}
import fwc.game.planningPhase.{OrderType, PlacedOrders}
import ujson.Value

import scala.collection.immutable.Seq
import scala.util.Try

case class ActionCleanUpAfterCombat(
                                     gameState: GameState,
                                   ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {

    val combat = gameState.combat
    val updatedArmies = `move winner's army to embattled tile` (combat)

    val updatedDiscardedCards = `discard cards used in combat` (combat)

    val updatedDiscardedCards2 = `restore wolf's cards if card 2 was used` (combat, updatedDiscardedCards)

    val updatedPlacedOrders = `move march order to embattled tile if rose won with card 0` (combat)

    val updatedPlacedOrders2 = `remove defender's order if he lost` (combat, updatedPlacedOrders)

    val updatedTokens = `give winner 2 power tokens if he had lion 2 card` (combat, updatedArmies)

    val numOfCastles = `count winner's castles` (combat, updatedArmies)

    val updatedGameState_ArmiesPlacedOrders = gameState.copy(
      armies = updatedArmies,
      placedOrders = updatedPlacedOrders2
    )

    val newPhase = `calculate new sub phase` (combat, numOfCastles, updatedGameState_ArmiesPlacedOrders, updatedArmies)

    updatedGameState_ArmiesPlacedOrders.copy(
      subPhase = newPhase,
      combat = if newPhase.isInstanceOf[SubPhaseResolveHouseCard] then combat else null,
      discardedHouseCards = 
        if newPhase.isInstanceOf[SubPhaseResolveHouseCard] 
        then updatedDiscardedCards2
        else updatedDiscardedCards2.resetDecksAfterCombat(combat),
      powerTokens = updatedTokens
    )
  }

  private def `calculate new sub phase` (combat: Combat, numOfCastles: TileNumber, updatedGameState_ArmiesPlacedOrders: GameState, updatedArmies: Armies) = {
    if numOfCastles >= 7
    then SubPhaseCalculateGameWinner(HouseType.getSeqOfAll)
    else if combat.winnerCard.exists(_.isMoose2)
      && {
      val footmenExistsFunc =
        (army: Seq[MilitaryUnit], support: Seq[TileNumber]) =>
          army.exists(_.unitType == MilitaryUnitType.Footmen)
            || support.foldLeft(false)(
            (acc, tn) =>
              updatedGameState_ArmiesPlacedOrders.armies(tn).foldLeft(false)(
                (acc2, mu: MilitaryUnit) =>
                  acc2 || (mu.unitType == MilitaryUnitType.Footmen && mu.house == HouseType.Moose)
              ) || acc
          )

      val hasAvailableMooseKnightsFunc =
        () => {
          val c = updatedArmies.foldLeft[TileNumber](0)(
            (acc: TileNumber, tileNumberArmy: (TileNumber, Seq[MilitaryUnit])) =>
              acc + tileNumberArmy._2.count(mu => mu.unitType == MilitaryUnitType.Knights && mu.house == HouseType.Moose)
          )
          c < gameRules.maxArmies(MilitaryUnitType.Knights)
        }

      if combat.winner.contains(combat.attackerHouse)
      then footmenExistsFunc(combat.attackerArmy, combat.attackerSupport)
        && hasAvailableMooseKnightsFunc()
      else footmenExistsFunc(combat.defenderArmy, combat.defenderSupport)
        && hasAvailableMooseKnightsFunc()
    }
    then SubPhaseResolveHouseCard(HouseType.Moose, 2) // todo looks like there is an infinite loop here (jvm crashes)
    else if combat.winnerCard.exists(_.isLion1)
    then SubPhaseResolveHouseCard(HouseType.Lion, 1)
    else if combat.winnerCard.exists(_.isMoose3) || combat.loserCard.exists(_.isMoose3)
    then SubPhaseResolveHouseCard(HouseType.Moose, 3)
    else NextOrderFinder.nextSubPhase(updatedGameState_ArmiesPlacedOrders, OrderType.March, combat.attackerHouse)
  }

  private def `count winner's castles`(combat: Combat, updatedArmies: Armies) = {
    updatedArmies.foldLeft(0)(
      (acc: TileNumber, cur: (TileNumber, Seq[MilitaryUnit])) =>
        val tile = gameRules.board(cur._1)
        if tile.musteringPoints > 0
          && cur._2.exists(_.house == combat.winner.head)
        then acc + 1
        else acc
    ) + {
      if Try[Seq[MilitaryUnit]] {
        updatedArmies(gameRules.board.find(_.homeOf == combat.winner.head).head.number)
      }.getOrElse(Seq()).isEmpty
      then 1
      else 0
    }
  }

  private def `give winner 2 power tokens if he had lion 2 card`(combat: Combat, updatedArmies: Armies) = {
    if combat.winnerCard.exists(_.isLion2)
    then gameState.powerTokens.addTokens(HouseType.Lion, 2, updatedArmies)
    else gameState.powerTokens
  }

  private def `remove defender's order if he lost`(combat: Combat, updatedPlacedOrders: PlacedOrders) = {
    if combat.winner.contains(combat.attackerHouse)
    then updatedPlacedOrders.removeOrder(combat.defenderHouse, combat.defenderTileNum)
    else updatedPlacedOrders
  }

  private def `move march order to embattled tile if rose won with card 0`(combat: Combat) = {
    if combat.winnerCard.exists(_.isRose0) && combat.winner.contains(combat.attackerHouse)
    then gameState.placedOrders.placeOrder(
      combat.attackerHouse,
      combat.defenderTileNum,
      combat.attackerOrder,
      0
    )
    else gameState.placedOrders
  }

  private def `restore wolf's cards if card 2 was used`(combat: Combat, updatedDiscardedCards: DiscardedHouseCards) = {
    if combat.loserCard.exists(_.isWolf2)
    then updatedDiscardedCards - HouseType.Wolf
    else updatedDiscardedCards
  }

  private def `discard cards used in combat`(combat: Combat) = {
    gameState.discardedHouseCards.concat(Map(
      combat.attackerHouse -> (
        if combat.attackerCard == null || gameState.discardedHouseCards(combat.attackerHouse).contains(combat.attackerCard.code) then gameState.discardedHouseCards(combat.attackerHouse)
        else gameState.discardedHouseCards(combat.attackerHouse) :+ combat.attackerCard.code
        ),
      combat.defenderHouse -> (
        if combat.defenderCard == null || gameState.discardedHouseCards(combat.defenderHouse).contains(combat.defenderCard.code) then gameState.discardedHouseCards(combat.defenderHouse)
        else gameState.discardedHouseCards(combat.defenderHouse) :+ combat.defenderCard.code
        )
    ))
  }

  private def `move winner's army to embattled tile`(combat: Combat) = {
    if combat.loserCard.exists(_.isPufferfish2) && combat.loser.contains(combat.defenderHouse)
    then gameState.armies + (combat.attackerTileNum ->
      (gameState.armies.getOrElse(combat.attackerTileNum, Seq()) ++ combat.attackerArmy)
      )
    else {
      if combat.winner.contains(combat.attackerHouse)
      then
        if combat.attackerArmy.nonEmpty
        then gameState.armies + (combat.defenderTileNum -> combat.attackerArmy)
        else gameState.armies - combat.defenderTileNum
      else if combat.defenderArmy.nonEmpty
      then gameState.armies + (combat.defenderTileNum -> combat.defenderArmy)
      else gameState.armies - combat.defenderTileNum
    }
  }

  override def toJson: Value = ujson.Obj(
    Action.actionTypeJsonKey -> "cleanUpAfterCombat",
  )
}

object ActionCleanUpAfterCombat extends JsonParsableAction {
  override def fromJson(gameState: GameState, json: Value): ActionCleanUpAfterCombat =
    ActionCleanUpAfterCombat(
      gameState,
    )

  def buildMessage(updatedGameState: GameState): ujson.Obj =
    ujson.Obj(
      "armies" -> updatedGameState.armies.toJson,
      "discardedHouseCards" -> updatedGameState.discardedHouseCards.toJson,
      "placedOrders" -> updatedGameState.placedOrders.toJson,
      "powerTokens" -> updatedGameState.powerTokens.toJson
    )
}
