package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseGameEnd(
                                        houseTypes: Seq[HouseType],
                                        mainPhase: MainPhase = MainPhase.Action
                                      ) 
  extends SubPhaseMultipleHouses(houseTypes, mainPhase) {
  def getSubPhaseName: String = "gameEnd"
}
