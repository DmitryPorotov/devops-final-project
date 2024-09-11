package fwc.gameLoading

import fwc.JsonSerializable
import fwc.game.houses.*
import ujson.Value

case class HouseCard(
                      house: HouseType, 
                      code: Int, 
                      name: String, 
                      strength: Int, 
                      text: String = "", 
                      attack: Int = 0, 
                      defense: Int = 0
                    ) extends CardTrait, JsonSerializable {
  override def getCode: Int = code

  override def toJson: Value = ujson.Obj(
    "house" -> house.toString,
    "code" -> code,
    "name" -> name,
    "strength" -> strength,
    "text" -> text,
    "attack" -> attack,
    "defense" -> defense
  )

  def isWolf0: Boolean = house == HouseType.Wolf && code == 0
  def isWolf2: Boolean = house == HouseType.Wolf && code == 2
  def isWolf5: Boolean = house == HouseType.Wolf && code == 5
  def isWolf6: Boolean = house == HouseType.Wolf && code == 6
  def isKraken4: Boolean = house == HouseType.Kraken && code == 4
  def isMoose2: Boolean = house == HouseType.Moose && code == 2
  def isMoose3: Boolean = house == HouseType.Moose && code == 3
  def isMoose6: Boolean = house == HouseType.Moose && code == 6
  def isPufferfish2: Boolean = house == HouseType.PufferFish && code == 2
  def isRose0: Boolean = house == HouseType.Rose && code == 0
  def isLion1: Boolean = house == HouseType.Lion && code == 1
  def isLion2: Boolean = house == HouseType.Lion && code == 2
  def isLion4: Boolean = house == HouseType.Lion && code == 4

}
