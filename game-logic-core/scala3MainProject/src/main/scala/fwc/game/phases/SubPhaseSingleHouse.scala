package fwc.game.phases
import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.HouseType
import ujson.Value

abstract class SubPhaseSingleHouse(
                           houseType: HouseType,
                           mainPhase: MainPhase,
                         ) 
  extends SubPhase(mainPhase) {
  def getHouseType: HouseType = houseType
  override def toJson: Value = ujson.Obj(
    "mainPhase" -> mainPhase.toString,
    "subPhase" -> getSubPhaseName,
    "houseType" -> houseType.toString
  )
}
