package fwc.game.board

import fwc.{JsonParsable, JsonSerializable}
import fwc.game.houses.HouseType
import scala.util.Try
import ujson.Value

case class MilitaryUnit(
                         house: HouseType,
                         unitType: MilitaryUnitType,
                         isDefeated: Boolean = false,
                         garrisonDefensePoints: Int = 0
                       ) extends JsonSerializable {

  def toJson: ujson.Value = {
    val milUnit = ujson.Obj(
      "house" -> ujson.Str(house.toString),
      "type" -> ujson.Str(unitType.toString)
    )
    if isDefeated then milUnit.obj addOne ("isDefeated" -> ujson.Bool(isDefeated))
    if unitType == MilitaryUnitGarrison then milUnit.obj addOne ("defPoints" -> ujson.Num(garrisonDefensePoints))
    milUnit
  }

}

object MilitaryUnit extends JsonParsable {
  override def fromJson(json: Value): MilitaryUnit = {
    MilitaryUnit(
      HouseType.fromString(json.obj("house").str),
      MilitaryUnitType.fromString(json.obj("type").str),
      Try[Boolean](json.obj("isDefeated").bool).getOrElse(false),
      Try[Double](json.obj("defPoints").num).getOrElse(.0).toInt
    )
  }
}
