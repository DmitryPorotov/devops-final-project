package fwc.game.actions

import fwc.game.houses.HouseType

trait PlayerAction(houseType: HouseType) extends Action {
  def getHouseType: HouseType = houseType
}
