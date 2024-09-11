package fwc.game.actionPhase

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.HouseType
import ujson.Value

import scala.annotation.targetName

type CardCode = Int

extension (t: CardCode)
  def isValid: Boolean = t >= 0 && t < 7
  
case class DiscardedHouseCards(cards: Map[HouseType, Seq[CardCode]] = Map()) extends JsonSerializable {
  def toJson: ujson.Value = {
    ujson.Obj.from(
      cards.map((houseType, cardCodeSeq: Seq[CardCode]) => {
        houseType.toString -> ujson.Arr.from(cardCodeSeq)
      })
    )
  }

  export cards.{contains, getOrElse, map}
  
  def resetDecksAfterCombat(combat: Combat): DiscardedHouseCards = {
    copy(
      cards.map(
        (htc: (HouseType, Seq[Int])) =>
          if htc._2.size >= 7
          then 
            if combat.winnerCard.exists(_.house == htc._1)
            then htc._1 -> Seq(combat.winnerCard.head.code)
            else if combat.loserCard.exists(_.house == htc._1)
            then htc._1 -> Seq(combat.loserCard.head.code)
            else htc._1 -> Seq(htc._2.last) //note: this is an impossible option
          else htc
      )
    )
  }

  @targetName("updated")
  def +(kv: (HouseType, Seq[CardCode])): DiscardedHouseCards = copy(cards + kv)
  
  @targetName("removed")
  def -(houseType: HouseType): DiscardedHouseCards = copy(cards - houseType)
  
  def concat(that: collection.IterableOnce[(HouseType, Seq[CardCode])]): DiscardedHouseCards = copy(cards.concat(that))

  def apply(houseType: HouseType): Seq[CardCode] = cards.getOrElse(houseType, Seq())
}

object DiscardedHouseCards extends JsonParsable {
  override def fromJson(json: Value): DiscardedHouseCards = {
    DiscardedHouseCards(
      json.obj.map((house, cards: Value) =>
        HouseType.fromString(house) -> cards.arr.map(_.num.toInt).toSeq
      ).toMap
    )
  }
}
