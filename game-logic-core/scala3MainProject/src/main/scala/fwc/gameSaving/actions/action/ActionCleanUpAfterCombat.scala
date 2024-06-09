package fwc.gameSaving.actions.action

import fwc.JsonSerializable
import fwc.game.{GameState, gameRules}
import fwc.game.board.{MilitaryUnit, MilitaryUnitFootmen, MilitaryUnitKnights, TileNumber, TrackCourt}
import fwc.game.houses.{HouseLion, HouseMoose, HouseRose, HouseType, HouseWolf}
import fwc.game.phases.actionSubPhases.{SubPhaseCalculateGameWinner, SubPhaseCleanUpAfterCombat, SubPhaseResolveHouseCard}
import fwc.game.planningPhase.OrderMarch
import fwc.gameSaving.actions.{Action, ActionException, JsonParsableAction}
import ujson.Value

import scala.collection.immutable.Seq

case class ActionCleanUpAfterCombat(
                                     gameState: GameState,
                                   ) extends Action(gameState) with JsonSerializable {
  override def doAction(): GameState = {
//    if !gameState.subPhase.isInstanceOf[SubPhaseCleanUpAfterCombat]
//    then throw new ActionException("Wrong phase")

    val combat = gameState.combat
    val updatedArmies =
      if combat.loserCard.exists(_.isPufferfish2) && combat.loser.contains(combat.defenderHouse)
      then gameState.armies + (combat.attackerTileNum ->
        (gameState.armies.getOrElse(combat.attackerTileNum, Seq()) ++ combat.attackerArmy)
      )
      else gameState.armies + (combat.defenderTileNum ->
          (if combat.winner.contains(combat.attackerHouse)
          then combat.attackerArmy
          else combat.defenderArmy)
        )

    val updatedDiscardedCards =
      gameState.discardedHouseCards.concat(Map(
        combat.attackerHouse -> (
          if combat.attackerCard == null then gameState.discardedHouseCards(combat.attackerHouse)
          else gameState.discardedHouseCards(combat.attackerHouse) :+ combat.attackerCard.code
          ),
        combat.defenderHouse -> (
          if combat.defenderCard == null then gameState.discardedHouseCards(combat.defenderHouse)
          else gameState.discardedHouseCards(combat.defenderHouse) :+ combat.defenderCard.code
          )
      ))

    val updatedDiscardedCards2 =
      if combat.loserCard.exists(_.isWolf2)
      then updatedDiscardedCards - HouseWolf
      else updatedDiscardedCards

    val updatedPlacedOrders =
      if combat.winnerCard.exists(_.isRose0) && combat.winner.contains(combat.attackerHouse)
      then gameState.placedOrders.placeOrder(
        combat.attackerHouse,
        combat.defenderTileNum,
        combat.attackerOrder,
        0
      )
      else gameState.placedOrders

    val updatedPlacedOrders2 =
      if combat.winner.contains(combat.attackerHouse)
      then updatedPlacedOrders.removeOrder(combat.defenderHouse, combat.defenderTileNum)
      else updatedPlacedOrders

    val updatedTokens =
      if combat.winnerCard.exists(_.isLion2)
      then gameState.powerTokens.addTokens(HouseLion, 2, updatedArmies)
      else gameState.powerTokens

    val numOfCastles = updatedArmies.foldLeft(0)(
      (acc: Int, cur: (TileNumber, Seq[MilitaryUnit])) =>
        val tile = gameRules.board(cur._1)
        if tile.musteringPoints > 0
          && cur._2.exists(_.house == combat.winner.head)
        then acc + 1
        else acc
    ) + {
      if updatedArmies
        .getOrElse(gameRules.board.find(_.homeOf == combat.winner.head).head.number, Seq())
        .isEmpty
      then 1
      else 0
    }

    val newPhase =
      if numOfCastles >= 7
      then SubPhaseCalculateGameWinner()
      else
      if combat.winnerCard.exists(_.isMoose2)
        && {
        val footmenExistsFunc =
          (army: Seq[MilitaryUnit], support: Seq[Int]) =>
            army.exists(_.unitType == MilitaryUnitFootmen)
              || support.foldLeft(false)(
              (acc, tn) =>
                gameState.armies(tn).foldLeft(false)(
                  (acc2, mu: MilitaryUnit) =>
                    acc2 || (mu.unitType == MilitaryUnitFootmen && mu.house == HouseMoose)
                ) || acc
            )

        val hasAvailableMooseKnightsFunc =
          () => {
            val c = updatedArmies.foldLeft[Int](0)(
              (acc: Int, tileNumberArmy: (TileNumber, Seq[MilitaryUnit])) =>
                acc + tileNumberArmy._2.count(mu => mu.unitType == MilitaryUnitKnights && mu.house == HouseMoose)
            )
            c < gameRules.maxArmies(MilitaryUnitKnights)
          }

        if combat.winner.contains(combat.attackerHouse)
        then footmenExistsFunc(combat.attackerArmy, combat.attackerSupport)
          && hasAvailableMooseKnightsFunc()
        else footmenExistsFunc(combat.defenderArmy, combat.defenderSupport)
          && hasAvailableMooseKnightsFunc()
      }
      then SubPhaseResolveHouseCard(HouseMoose, 2)
      else if combat.winnerCard.exists(_.isLion1)
      then SubPhaseResolveHouseCard(HouseLion, 1)
      else if (combat.winnerCard.exists(_.isMoose3) || combat.loserCard.exists(_.isMoose3))
        && {
          val loserDiscardedCards: Seq[Int] = gameState.discardedHouseCards.getOrElse(combat.loser.head, Seq())
          loserDiscardedCards.size < 7
        }
      then SubPhaseResolveHouseCard(HouseMoose, 3)
      else NextOrderFinder.nextSubPhase(gameState, OrderMarch, combat.attackerHouse)

    gameState.copy(
      subPhase = newPhase,
      armies = updatedArmies,
      combat = if newPhase.isInstanceOf[SubPhaseResolveHouseCard] then combat else null,
      discardedHouseCards = 
        if newPhase.isInstanceOf[SubPhaseResolveHouseCard] 
        then updatedDiscardedCards2
        else updatedDiscardedCards2.resetDecksAfterCombat(combat),
      placedOrders = updatedPlacedOrders2,
      powerTokens = updatedTokens
    )
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
