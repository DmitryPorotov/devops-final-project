package fwc.gameSaving.actions.action

import fwc.game.board.{MilitaryUnitShips, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.{GameState, gameRules}
import fwc.gameLoading.{BoardTileLand, BoardTilePort, BoardTileSea}

trait MarchRetreatTrait(gameState: GameState, houseType: HouseType) {
  def hasPath(sourceTileNumber: TileNumber, targetTileNumber: TileNumber): Boolean = {
    if gameRules.board(sourceTileNumber).isNeighbourOf(targetTileNumber)
    then return true
    try {
      def dfsRecursion(tileNum: TileNumber, visited: Seq[TileNumber]): Boolean = {
        gameRules.board(tileNum).neighbourTiles.foldLeft(false)(
          (acc, cur) =>
            if cur == targetTileNumber then throw new FoundPathException
            val army = gameState.armies.get(cur)
            val hasShipAtSea =
              if army.isEmpty
              then false
              else
                army.head.nonEmpty
                  && army.head.head.house == houseType
                  && army.head.head.unitType == MilitaryUnitShips
                  && gameRules.board(cur).tileType != BoardTilePort

            if !visited.contains(cur) && hasShipAtSea
            then dfsRecursion(cur, visited :+ cur)
            else acc
        )
      }

      dfsRecursion(sourceTileNumber, Seq(sourceTileNumber))
    }
    catch {
      case _: FoundPathException => true
      case e: Throwable => throw e
    }
  }

  def getAllNeighboursBySea(sourceTileNumber: TileNumber): Seq[TileNumber] = {
    val sourceTile = gameRules.board(sourceTileNumber)
    if sourceTile.tileType == BoardTileSea
    then return sourceTile.neighbourTiles.filter(tn => gameRules.board(tn).tileType != BoardTileLand)
    if sourceTile.tileType == BoardTilePort
    then return sourceTile.neighbourTiles.filter(tn => gameRules.board(tn).tileType == BoardTileSea)

    def dfsRecursion(tileNum: TileNumber, visited: Seq[TileNumber]): Set[TileNumber] = {
      gameRules.board(tileNum).neighbourTiles.foldLeft(Set())(
        (acc, cur) =>
          val acc2 =
            if gameRules.board(cur).tileType == BoardTileLand
            then acc + cur
            else acc
          val army = gameState.armies.get(cur)
          val hasShipAtSea =
            if army.isEmpty
            then false
            else
              army.head.nonEmpty
                && army.head.head.house == houseType
                && army.head.head.unitType == MilitaryUnitShips
                && gameRules.board(cur).tileType != BoardTilePort

          if !visited.contains(cur) && hasShipAtSea
          then dfsRecursion(cur, visited :+ cur) ++ acc2
          else acc2
      )
    }

    dfsRecursion(sourceTileNumber, Seq(sourceTileNumber)).toSeq
  }
}
