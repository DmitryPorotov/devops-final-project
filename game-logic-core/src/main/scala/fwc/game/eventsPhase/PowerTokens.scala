package fwc.game.eventsPhase

import fwc.game.board.{Armies, MilitaryUnit, MilitaryUnitPowerToken, TileNumber}
import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.*
import ujson.Value
import fwc.game.{FWCException, gameRules}

import scala.annotation.{tailrec, targetName}
import scala.collection.mutable

case class PowerTokens(tokens: Map[HouseType, Int] = Map()) extends JsonSerializable {
  def toJson: ujson.Value = {
    ujson.Obj(
      mutable.LinkedHashMap.from(
        tokens.map((houseType, numOfTokens) => houseType.toString -> ujson.Num(numOfTokens))
      )
    )
  }

  def transferOneToken(fromHouse: HouseType, toHouse: HouseType, armies: Armies): PowerTokens = {
    copy(
      tokens
        + (fromHouse -> (if tokens(fromHouse) - 1 <= 0 then 0 else tokens(fromHouse) - 1))
    ).addTokens(toHouse, 1, armies)
  }

  export tokens.{apply, map}

  @targetName("updated")
  def +(kv: (HouseType, Int)): PowerTokens = copy(tokens + kv)

  def addTokens(houseType: HouseType, amount: Int, armies: Armies): PowerTokens = {
    val sum = tokens(houseType) + amount
    if sum < 0
    then throw new FWCException(s"House $houseType does not have enough tokens")

    val tokensOnMap = armies.foldLeft(0)(
      (acc, tileNumberArmy: (TileNumber, Seq[MilitaryUnit])) =>
        acc + tileNumberArmy._2.count(_.unitType == MilitaryUnitPowerToken)
    )
    
    if ((sum + tokensOnMap) > gameRules.maxArmies(MilitaryUnitPowerToken))
    then copy(tokens + (houseType -> gameRules.maxArmies(MilitaryUnitPowerToken)))
    else copy(tokens + (houseType -> sum))
  }
}

object PowerTokens extends JsonParsable {
  def initialize(num: Int): PowerTokens = PowerTokens(
    Map(
      HouseLion -> num,
      HouseKraken -> num,
      HousePufferfish -> num,
      HouseWolf -> num,
      HouseMoose -> num,
      HouseRose -> num
    )
  )

  override def fromJson(json: Value): PowerTokens = {
    PowerTokens(
      json.obj.map((house, num: Value) =>
        HouseType.fromString(house) -> num.num.toInt
      ).toMap
    )
  }
}