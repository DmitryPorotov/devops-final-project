package fwc.game.phases.actionSubPhases

import fwc.game.board.TileNumber
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseSingleHouse}
import ujson.Value

case class SubPhaseLeavePowerTokenAtTile(
                                          override val houseType: HouseType,
                                          tileNumber: TileNumber,
                                          override val mainPhase: MainPhase = MainPhase.Action
                                        ) extends SubPhase(mainPhase) with SubPhaseSingleHouse(
  houseType, mainPhase
) {
  override def toJson: Value = {
    val json = super.toJson
    json.obj.addOne("tileNumber", tileNumber)
    json
  }

  def getSubPhaseName: String = "leavePowerTokenAtTile"
}
