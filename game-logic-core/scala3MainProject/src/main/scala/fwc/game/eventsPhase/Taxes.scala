package fwc.game.eventsPhase

import fwc.game.board.{Armies, TileNumber}
import fwc.game.houses.HouseType
import fwc.game.gameRules
import fwc.gameLoading.BoardTilePort

object Taxes {
  def collectTaxes(armies: Armies, powerTokens: PowerTokens): PowerTokens = {
    val toTax: Iterable[(HouseType, TileNumber, Int)] = (
      for (
        army <- armies;
        tile <- gameRules.board if army._1 == tile.number
          && (tile.powerPoints > 0 || tile.tileType == BoardTilePort)
          && army._2.head.house != HouseType.Neutral
      )
      yield (army._2.head.house, tile.number, if tile.powerPoints > 0 then tile.powerPoints else 1)
    )
    ++ gameRules.board.view
      .filter(_.homeOf != null)
      .map(
        bt =>
         (bt.homeOf,
          bt.number,
          if armies.getOrElse(bt.number, Seq()).isEmpty then bt.powerPoints else 0
         )
      )

    val summed = toTax.foldLeft {
      Map[HouseType, Int]()
    }((acc, cur) => {
      acc +
        (cur._1 -> (acc.getOrElse(cur._1, 0) + cur._3))
    })

    PowerTokens(powerTokens.map((h, n) => {
        h -> (n + summed.getOrElse(h, 0))
      })
    )
  }


}
