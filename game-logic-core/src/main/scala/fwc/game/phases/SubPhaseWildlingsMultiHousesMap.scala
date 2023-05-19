package fwc.game.phases

import fwc.game.houses.HouseType

import scala.collection.mutable

trait SubPhaseWildlingsMultiHousesMap(
                                       houseTypes: Map[HouseType, Int],
                                     )
  extends SubPhaseNoHouse {

  override def toJson: ujson.Value =
    val json = super.toJson
    json.obj.addOne(
      "houseTypes" -> mutable.LinkedHashMap.from(
        houseTypes.map((ht, num) => ht.toString -> num)
      )
    )
    json
}
