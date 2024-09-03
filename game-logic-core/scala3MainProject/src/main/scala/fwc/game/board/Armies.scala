package fwc.game.board

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.board.TileNumber
import fwc.game.houses.HouseType
import fwc.game.{FWCException, gameRules}
import ujson.Value
import enrichment.ExtSeq
import fwc.game.eventsPhase.Supplies
import fwc.gameLoading.BoardTile

import scala.annotation.{tailrec, targetName}

case class Armies(private val armies: Map[TileNumber, Seq[MilitaryUnit]] = Map()) extends JsonSerializable {

  export armies.{contains, count, exists, filter, flatMap, flatten, foldLeft, get, getOrElse, map, withFilter}
  def toJson: ujson.Value = {
    ujson.Obj(
      upickle.core.LinkedHashMap(
        armies.map((tileNum: TileNumber, units: Seq[MilitaryUnit]) => {
          tileNum.toString -> ujson.Value(units.map(_.toJson))
        })
      )
    )
  }

  def hasCommandableHouseArmyOnTile(tileNum: TileNumber, house: HouseType): Boolean = {
    armies.contains(tileNum) && armies(tileNum).exists(mu => mu.house == house && mu.unitType.canBeMustered)
  }

  def getControlledCastleTilesByHouse(houseType: HouseType): Seq[BoardTile] = {
    armies.foldLeft[Seq[BoardTile]](Seq())((acc, cur) => {
      val (tileNum, army) = cur
      if army.nonEmpty && army.head.house == houseType && gameRules.board(tileNum).musteringPoints > 0
      then acc :+ gameRules.board(tileNum)
      else acc
    })
  }
  
  def moveArmy(
                houseType: HouseType,
                sourceTileNum: TileNumber,
                army: Seq[MilitaryUnit],
                targetTileNum: TileNumber,
                houseSupplies: Supplies
              ): Armies = {
    if !armies.contains(sourceTileNum)
    then throw new FWCException("There is no army at source tile")

    if army.exists(_.isDefeated)
    then throw new FWCException("Cannot move routed (defeated) units")

    val armiesNoToken =
      if armies.contains(targetTileNum)
        && armies(targetTileNum).size == 1
        && armies(targetTileNum).head.house != houseType
        && armies(targetTileNum).head.unitType == MilitaryUnitType.PowerToken
      then armies - targetTileNum
      else armies

    if armiesNoToken.contains(targetTileNum)
      && armiesNoToken(targetTileNum).nonEmpty
      && armiesNoToken(targetTileNum).head.house != houseType
    then throw new FWCException("Cannot move army to an enemy tile")

    val newArmiesAtSource = Armies.subtractArmies(armiesNoToken(sourceTileNum), army)

    val newArmiesTmp =
      if newArmiesAtSource.nonEmpty
      then armiesNoToken + (sourceTileNum -> newArmiesAtSource)
      else armiesNoToken - sourceTileNum

    val newArmies = Armies(newArmiesTmp
      + (targetTileNum -> (
        newArmiesTmp.getOrElse(targetTileNum, Seq())
          ++ army
      ))
    )

    val toConsolidate = Supplies.findArmiesToConsolidate(newArmies, houseSupplies, houseType)
    if toConsolidate.nonEmpty && toConsolidate(houseType).nonEmpty
    then throw new FWCException(s"Not enough supplies to move army to ${gameRules.board(targetTileNum).name} ($targetTileNum)")
    newArmies
  }

  def disbandMilitaryUnit(tileNumber: TileNumber, militaryUnit: MilitaryUnit): Armies = {
    if !apply(tileNumber).contains(militaryUnit)
    then throw new FWCException(s"There are no ${militaryUnit.unitType} of house ${militaryUnit.house} at tile $tileNumber")
    
    val army = Armies.subtractArmies(armies(tileNumber), Seq(militaryUnit))
    if army.isEmpty
    then this - tileNumber
    else this + (tileNumber -> army)
  }
  def apply(tileNumber: TileNumber): Seq[MilitaryUnit] = {
    if !armies.contains(tileNumber)
    then throw new FWCException(s"There are no armies at tile $tileNumber")
    else armies(tileNumber)
  }
  
  def countUnitsByTypeAndHouse(unitType: MilitaryUnitType, houseType: HouseType): Int = {
    armies.foldLeft(0)(
      (acc, cur: (TileNumber, Seq[MilitaryUnit])) =>
        acc + cur._2.count(mu => mu.unitType == unitType && mu.house == houseType)
    )
  }

  @targetName("updated")
  def +(kv: (TileNumber, Seq[MilitaryUnit])): Armies = copy(armies + kv)

  @targetName("removed")
  def -(key: TileNumber): Armies = copy(armies - key)
}

object Armies extends JsonParsable {
  def fromJson(json: Value): Armies = {
    Armies(
      json.obj.map((tileNum: String, army: ujson.Value) => {
        tileNum.toInt -> army.arr.map(mu => MilitaryUnit.fromJson(mu)).toSeq
      }).toMap
    )
  }

  @tailrec
  final def subtractArmies(from: Seq[MilitaryUnit], toSubtract: Seq[MilitaryUnit]): Seq[MilitaryUnit] = {
    if toSubtract.isEmpty
    then from
    else if from.contains(toSubtract.head)
    then subtractArmies(from.deleteFirstMatch(toSubtract.head), toSubtract.tail)
    else throw new FWCException(s"Source tile has not enough units (${toSubtract.head.unitType})")
  }
}
