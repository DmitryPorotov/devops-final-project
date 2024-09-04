package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseCollectTaxes(
                                 houseTypes: Seq[HouseType],
                                 mainPhase: MainPhase = MainPhase.RoundEvents
                               ) 
  extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase) 
  with SubPhasePassive {
    def getSubPhaseName: String = "collectTaxes"
}
