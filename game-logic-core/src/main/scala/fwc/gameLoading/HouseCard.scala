package fwc.gameLoading

import fwc.game.houses.*

case class HouseCard(
                      house: HouseType, 
                      code: Int, 
                      name: String, 
                      strength: Int, 
                      text: String = "", 
                      attack: Int = 0, 
                      defense: Int = 0
                    ) extends CardTrait {
  override def getCode: Int = code

  def isWolf0: Boolean = house == HouseWolf && code == 0
  def isWolf2: Boolean = house == HouseWolf && code == 2
  def isWolf5: Boolean = house == HouseWolf && code == 5
  def isWolf6: Boolean = house == HouseWolf && code == 6
  def isKraken4: Boolean = house == HouseKraken && code == 4
  def isMoose2: Boolean = house == HouseMoose && code == 2
  def isMoose3: Boolean = house == HouseMoose && code == 3
  def isMoose6: Boolean = house == HouseMoose && code == 6
  def isPufferfish2: Boolean = house == HousePufferfish && code == 2
  def isRose0: Boolean = house == HouseRose && code == 0
  def isLion1: Boolean = house == HouseLion && code == 1
  def isLion2: Boolean = house == HouseLion && code == 2

}
