package fwc.game.eventsPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.gameRules
import fwc.game.houses.*
import fwc.game.board.{Armies, MilitaryUnitType}
import fwc.gameLoading.BoardStart
import ujson.Value

import scala.annotation.{tailrec, targetName}

case class Supplies(supplies: Map[HouseType, Int]) extends JsonSerializable {
  
  export supplies.{apply, filter, get, getOrElse}
  def toJson: ujson.Value = {
    ujson.Obj(upickle.core.LinkedHashMap{
      supplies.map((houseType, num) => houseType.toString -> ujson.Num(num))
    })
  }

  @targetName("updated")
  def +(kv: (HouseType, Int)): Supplies = copy(supplies + kv)

  @targetName("removed")
  def -(key: HouseType): Supplies = copy(supplies - key)
  
  @targetName("concat")
  def ++(xs: collection.IterableOnce[(HouseType, Int)]): Supplies = copy(supplies ++ xs)
}

object Supplies extends JsonParsable {
  def initialize(boardStart: Seq[BoardStart]): Supplies = {
    Supplies(
      boardStart.view
        .map(bs => bs.house -> bs.tracks.supply)
        .filter(_._1 != HouseType.Neutral)
        .toMap
    )
  }

  def recalculateSupplyTrack(armies: Armies): Supplies = {
    val tilesWithArmiesAndEmptyHomeTiles = (for (
      army <- armies;
      tile <- gameRules.board if tile.number == army._1
    ) yield {
      (army._2.head.house, tile.supplyPoints, tile.number)
    })
    ++ gameRules.board.view
      .filter(_.homeOf != null)
      .map(
        bt =>
          (bt.homeOf,
            if armies.getOrElse(bt.number, Seq()).isEmpty then bt.supplyPoints else 0,
            bt.number
          )
      )

    Supplies(
      tilesWithArmiesAndEmptyHomeTiles.foldLeft(Map[HouseType, Int]())((acc, cur) => {
        val sum = cur._2 + acc.getOrElse(cur._1, 0)
        acc + (cur._1 -> (if sum > 6 then 6 else sum))
      })
    )
  }

  def findArmiesToConsolidate(
                               armies: Armies,
                               houseSupplies: Supplies,
                               houseType: HouseType = null
                             ): Map[HouseType, Seq[Int]] = {

    type tileNum = Int
    type armySize = Int
    val houseToNumArmies = armies.foldLeft(Map[HouseType, Seq[(tileNum, armySize)]]())((acc, cur) => {
      val curTile = (cur._1, cur._2.count(mu => 
        mu.unitType != MilitaryUnitType.Garrison && mu.unitType != MilitaryUnitType.PowerToken))

      if (houseType != null && houseType != cur._2.head.house)
        acc
      else if (curTile._2 < 2)
        acc
      else
        acc + (cur._2.head.house -> (acc.getOrElse(cur._2.head.house, Seq[(tileNum, armySize)]()) :+ curTile))
    }).map((h, a: Seq[(tileNum, armySize)]) => {
      h -> a.sortWith((t1, t2) => t1._2 > t2._2)
    })

    def compareUsage(current: Seq[(tileNum, armySize)], permitted: Seq[Int]): Seq[Int] = {
      @tailrec
      def rec(c: Seq[(tileNum, armySize)], p: Seq[Int]): Int = {
        if (c.isEmpty)
          Int.MaxValue
        else if (p.isEmpty)
          Int.MinValue
        else if (c.head._2 > p.head)
          c.head._2
        else
          rec(c.tail, p.tail)
      }

      val largestForbiddenArmySize = rec(current, permitted)
      current.view.filter(_._2 >= largestForbiddenArmySize).map(_._1).toSeq
    }

    houseToNumArmies.map((house: HouseType, armies1: Seq[(tileNum, armySize)]) => {
      val usage = gameRules.supplyUsage(houseSupplies.supplies(house))
      house -> compareUsage(armies1, usage)
    })

  }

  def getHouseToConsolidate(toConsolidate: Map[HouseType, Seq[Int]], throneTrack: Seq[HouseType]): HouseType = {
    extension (ht1: HouseType)
      def isHigherOnThroneTrackThan(ht2: HouseType): Boolean =
        ht1.isHigherOnTrack(throneTrack)(ht2)

    val sortedArmies = toConsolidate.toSeq.sortWith(
      (a, b) => a._1 isHigherOnThroneTrackThan b._1
    )
    sortedArmies.head._1
  }
  override def fromJson(json: Value): Supplies = {
    Supplies(
      json.obj.map((house, num: Value) =>
        HouseType.fromString(house) -> num.num.toInt
      ).toMap
    )
  }
}
