package fwc.game.phases
import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.HouseType
import ujson.Value

trait SubPhaseSingleHouse(
                           houseType: HouseType,
                           mainPhase: MainPhase
                         ) extends SubPhase {
  override def toJson: Value = ujson.Obj(
    "mainPhase" -> mainPhase.toString,
    "subPhase" -> getSubPhaseName,
    "houseType" -> houseType.toString
  )
}
