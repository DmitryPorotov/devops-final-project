package fwc.game.phases

import fwc.game.houses.HouseType

import scala.collection.mutable

trait SubPhaseWildlingsMultiHousesMap(
                                       houseTypes: Map[HouseType, Int],
                                     ) 
{

  def toJson: ujson.Value = ujson.Obj(
      "houseTypes" -> mutable.LinkedHashMap.from(
        houseTypes.map((ht, num) => ht.toString -> num)
      )
    )
}
