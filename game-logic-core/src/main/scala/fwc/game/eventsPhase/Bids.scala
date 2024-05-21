package fwc.game.eventsPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.HouseType
import ujson.Value

import scala.annotation.targetName
import scala.collection.mutable
import scala.util.boundary

case class Bids(private val bids: Map[HouseType, Int] = Map()) extends JsonSerializable {
  def toJson: ujson.Value = ujson.Obj.from(
    mutable.LinkedHashMap.from(
      bids.map((houseType, num) => houseType.toString -> ujson.Num(num))
    )
  )

  export bids.{apply, foldLeft, get, getOrElse, size, toSeq}

  @targetName("updated")
  def +(kv: (HouseType, Int)): Bids = copy(bids + kv)

  @targetName("removed")
  def -(key: HouseType): Bids = copy(bids - key)

  def validateTieResolution(resolution: Seq[HouseType]): Boolean = boundary {
    val sortedBids = bids.toSeq.sortWith((a, b) => b._2 < a._2)
    val uniqueBids: Set[Int] = sortedBids.foldLeft(Set())((acc, cur) => acc + cur._2)
    val possiblePositions = uniqueBids.foldLeft(Map[HouseType, Seq[Int]]())(
      (acc, cur) => {
        val housesWithThisBid = sortedBids.filter(_._2 == cur)
        val indesesOfThisBid: Seq[Int] = housesWithThisBid.foldLeft(Seq[Int]())(
          (acc2, cur2) => acc2 :+ sortedBids.indexOf(cur2)
        )
        acc ++ housesWithThisBid.foldLeft(Map[HouseType, Seq[Int]]())(
          (acc3, cur3) => acc3 + (cur3._1 -> indesesOfThisBid)
        )
      }
    )
    resolution.foreach(houseType =>
      if !possiblePositions(houseType).contains(resolution.indexOf(houseType))
      then boundary.break(false)
    )
    
    true
  }

  def getLoserOrWinnerCandidatesInWildlingsBids(wildlingsCounter: Int): (Boolean, Seq[HouseType]) = {
    val sum = bids.foldLeft(0)((acc,cur) => acc + cur._2)
    val sortedBids = bids.toSeq.sortWith((a, b) => b._2 < a._2)
    val isWin = sum >= wildlingsCounter
    val bidToSearch =
      if isWin
      then sortedBids.head._2
      else sortedBids.last._2
    (isWin, sortedBids.view.filter(_._2 == bidToSearch).map(_._1).toSeq)
  }
}

object Bids extends JsonParsable {
  override def fromJson(json: Value): Bids = {
    Bids(
      json.obj.map((house, numTokens: Value) => 
        HouseType.fromString(house) -> numTokens.num.toInt 
      ).toMap
    )
  }
}