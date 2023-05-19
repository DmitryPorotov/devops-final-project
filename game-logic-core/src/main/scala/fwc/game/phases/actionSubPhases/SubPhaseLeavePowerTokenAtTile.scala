package fwc.game.phases.actionSubPhases

import fwc.game.board.TileNumber
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhaseSingleHouse}
import ujson.Value

case class SubPhaseLeavePowerTokenAtTile(
                                          houseType: HouseType,
                                          tileNumber: TileNumber,
                                          mainPhase: MainPhase = PhaseAction
                                        ) extends SubPhaseSingleHouse(
  houseType, mainPhase
) {
  override def toJson: Value = {
    val json = super.toJson
    json.obj.addOne("tileNumber", tileNumber)
    json
  }

  def getSubPhaseName: String = "leavePowerTokenAtTile"
}
