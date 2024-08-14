package fwc.game.eventsPhase

import fwc.game.{GameRules, GameState, gameRules}
import fwc.game.board.*
import fwc.game.houses.*
import fwc.gameLoading.{BoardTile, BoardTileLand}
import enrichment.ExtSeq
import fwc.game.board.TileNumber

import scala.annotation.tailrec

object Mustering {

  def musterGroundUnit(
                        musteringTileNumber: TileNumber,
                        militaryUnitToMuster: MilitaryUnit,
                        gameState: GameState,
                        isUpgrade: Boolean = false
                        ): (Armies, UsedMusteringPoints) = {
    val musteringBoardTile = gameRules.board(musteringTileNumber)

    commonChecks(militaryUnitToMuster, gameState.armies,musteringBoardTile)

    val armyAtTile = gameState.armies.getOrElse(musteringTileNumber, Seq())

    if (armyAtTile.isEmpty && musteringBoardTile.homeOf != militaryUnitToMuster.house)
      || (armyAtTile.nonEmpty && armyAtTile.head.house != militaryUnitToMuster.house)
    then throw new MusteringException(s"This tile does not belong to ${militaryUnitToMuster.house}")

    if isUpgrade && !armyAtTile.exists(_.unitType == MilitaryUnitType.Footmen)
    then throw new MusteringException("This tile has no Footmen to upgrade")

    if isUpgrade && militaryUnitToMuster.unitType.musteringPoints != 2
    then throw new MusteringException("A knight or a siege engine should be mustered when upgrading.")
    
    val newArmyAtTile =
      if isUpgrade
      then armyAtTile.deleteFirstMatch {
          MilitaryUnit(militaryUnitToMuster.house, MilitaryUnitType.Footmen)
        }
      else armyAtTile

    val musteringPointsToBeUsed = militaryUnitToMuster.unitType.musteringPoints
      - (if isUpgrade then 1 else 0)

    doMastering(gameState, militaryUnitToMuster, musteringBoardTile, musteringBoardTile, newArmyAtTile, musteringPointsToBeUsed)

  }

  private def commonChecks(militaryUnitToMuster: MilitaryUnit, armies: Armies, musteringBoardTile: BoardTile): Unit = {
    checkUnitCanBeMustered(militaryUnitToMuster)
    checkUnitCountIsNotReached(militaryUnitToMuster, armies)
    checkMusteringTileHasMusteringPoints(musteringBoardTile)
  }

  private def checkUnitCanBeMustered(militaryUnitToMuster: MilitaryUnit): Unit = {
    if militaryUnitToMuster.unitType.musteringPoints < 0
    then throw new MusteringException(s"Can't muster a ${militaryUnitToMuster.unitType}")
  }
  
  private def checkUnitCountIsNotReached(
                                          militaryUnitToMuster: MilitaryUnit,
                                          armies: Armies
                                        ): Unit = {
    if armies.flatten(_._2).count(u => {
        (u.house == militaryUnitToMuster.house) && (u.unitType == militaryUnitToMuster.unitType)
      }) >= gameRules.maxArmies(militaryUnitToMuster.unitType)
    then throw new MusteringException(s"Maximum count of ${militaryUnitToMuster.unitType} reached")
  }

  private def checkMusteringTileHasMusteringPoints(
                                          musteringBoardTile: BoardTile
                                        ): Unit = {
    if musteringBoardTile.musteringPoints == 0
    then throw new MusteringException(s"'${musteringBoardTile.name}' has no Stronghold or Castle")
  }

  private def doMastering(
                           gameState: GameState,
                           militaryUnitToMuster: MilitaryUnit,
                           musteringBoardTile: BoardTile,
                           targetBoardTile: BoardTile,
                           newArmyAtTile: Seq[MilitaryUnit],
                           musteringPointsToBeUsed: Int
                         ) = {
    val usedPointAtTile = gameState.usedMusteringPoints.points.getOrElse(musteringBoardTile, 0)
    if (musteringBoardTile.musteringPoints - usedPointAtTile)
      < musteringPointsToBeUsed
    then throw new MusteringException(s"Not enough points to muster ${militaryUnitToMuster.unitType}")

    val newArmies = gameState.armies + (targetBoardTile.number -> (newArmyAtTile :+ militaryUnitToMuster))
    val armiesToConsolidate = Supplies.findArmiesToConsolidate(newArmies, gameState.supplies, militaryUnitToMuster.house)

    if armiesToConsolidate.getOrElse(militaryUnitToMuster.house, Map()).nonEmpty
    then throw new MusteringException(
        s"House ${militaryUnitToMuster.house} does not have enough supplies to muster ${militaryUnitToMuster.unitType}"
      )

//    gameState.copy(
//      armies = newArmies,
//      usedMusteringPoints = UsedMusteringPoints(
//        gameState.usedMusteringPoints.points +
//        (
//          musteringBoardTile ->
//            (
//              usedPointAtTile
//                + musteringPointsToBeUsed
//            )
//        )
//      )
//    )
    (
      newArmies,
      UsedMusteringPoints(
        gameState.usedMusteringPoints.points +
          (
            musteringBoardTile ->
              (
                usedPointAtTile
                  + musteringPointsToBeUsed
                )
            )
      )
    )
  }

  def musterShips(
                 musteringTileNumber: Int,
                 musteringTargetTileNumber: Int,
                 militaryUnitToMuster: MilitaryUnit,
                 gameState: GameState
                 ): (Armies, UsedMusteringPoints) = {
    if militaryUnitToMuster.unitType != MilitaryUnitType.Ships
    then throw new MusteringException("This function only musters ships")

    val targetBoardTile = gameRules.board(musteringTargetTileNumber)
    val musteringBoardTile = gameRules.board(musteringTileNumber)

    commonChecks(militaryUnitToMuster, gameState.armies, musteringBoardTile)

    if targetBoardTile.tileType == BoardTileLand
    then throw new MusteringException("A ship must be mustered on a sea or in a port")

    val armyAtTargetTile = gameState.armies.getOrElse(musteringTargetTileNumber, Seq())

    if armyAtTargetTile.nonEmpty && armyAtTargetTile.head.house != militaryUnitToMuster.house
    then throw new MusteringException("Can't muster ships if there are enemy ships in the sea")

    if !musteringBoardTile.isNeighbourOf(targetBoardTile)
    then throw new MusteringException(s"${musteringBoardTile.name} is not a neighbor of ${targetBoardTile.name}")

    doMastering(gameState, militaryUnitToMuster, musteringBoardTile, targetBoardTile, armyAtTargetTile, MilitaryUnitType.Ships.musteringPoints)
  }
}
