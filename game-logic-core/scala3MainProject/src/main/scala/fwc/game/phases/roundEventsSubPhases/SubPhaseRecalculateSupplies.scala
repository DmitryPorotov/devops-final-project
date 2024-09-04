package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseRecalculateSupplies(
                                        houseTypes: Seq[HouseType] = HouseType.getSeqOfAll,
                                      override val mainPhase: MainPhase = MainPhase.RoundEvents
                                      )extends SubPhase(mainPhase)
  with SubPhasePassive(mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase)
  {
    override def toJson: Value = super.toJson
    def getSubPhaseName: String = "recalculateSupplies"
}
