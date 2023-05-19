package fwc.game

import fwc.game.houses.HouseType
import fwc.gameLoading.{BoardStart, BoardStartTile, BoardTile}
import scala.util.{Failure, Success, Try}

package object board {

  def initializeArmies(startTile: Seq[BoardStart]): Armies = {
    val flatStartTiles =
      for (
        st <- startTile;
        tile <- st.map
      )
      yield (tile, st.house)

    def makeArmy(boardStartTile: (BoardStartTile, HouseType)): Seq[MilitaryUnit] = {
      List.fill(boardStartTile._1.ships)(MilitaryUnit(boardStartTile._2, MilitaryUnitShips))
        ++ List.fill(boardStartTile._1.footmen)(MilitaryUnit(boardStartTile._2, MilitaryUnitFootmen))
        ++ List.fill(boardStartTile._1.knights)(MilitaryUnit(boardStartTile._2, MilitaryUnitKnights))
        ++ (
        if boardStartTile._1.garrison > 0
        then  List(MilitaryUnit(boardStartTile._2, MilitaryUnitGarrison,false, boardStartTile._1.garrison))
        else List.empty
        )
    }

    val armies: Map[Int, Seq[MilitaryUnit]] = flatStartTiles.view.map((fst: (BoardStartTile, HouseType)) =>
      fst._1.tileNumber -> makeArmy(fst)
    ).toMap

    Armies(armies)
  }

}
