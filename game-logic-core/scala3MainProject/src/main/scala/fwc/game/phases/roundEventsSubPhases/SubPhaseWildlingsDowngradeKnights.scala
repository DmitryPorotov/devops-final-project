package fwc.game.phases.roundEventsSubPhases

import enrichment.ExtUPickleHashMap
import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseWildlingsDowngradeKnights(
                                              houseTypes: Map[HouseType, Int],
                                             mainPhase: MainPhase = MainPhase.RoundEvents
                                            )
  extends SubPhase(mainPhase)
    with SubPhaseWildlingsMultiHousesMap(houseTypes)
 {

  override def getSubPhaseName: String = "wildlingsDowngradeKnights"

   override def toJson: Value = {
     val json = super.toJson
     json.obj.addPairs(
       "mainPhase" -> mainPhase.toString,
       "subPhase" -> getSubPhaseName,
     )
     json
   }
}
