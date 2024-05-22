package fwc.gameLoading

import fwc.JsonSerializable
import ujson.Value

case class TidesOfBattleCard(
                             code: Int, 
                             power: Int, 
                             death: Boolean = false, 
                             attack: Boolean = false, 
                             defense: Boolean = false
                           ) extends CardTrait with JsonSerializable{
  override def getCode: Int = code

  override def toJson: Value =
    val json = ujson.Obj(
      "code" -> code,
      "power" -> power,
    )
    if death
    then json.obj.addOne("death" -> death)
    if attack
    then json.obj.addOne("attack" -> attack)
    if defense
    then json.obj.addOne("defense" -> defense)
    json
}
       
