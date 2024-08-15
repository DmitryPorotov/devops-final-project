package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseRoundEvents, SubPhase, SubPhaseMultipleHouses, SubPhasePassive}
import ujson.Value

case class SubPhaseCollectTaxes(
                                 houseTypes: Seq[HouseType],
                                 mainPhase: MainPhase = PhaseRoundEvents
                               ) extends SubPhase(mainPhase)
  with SubPhasePassive(mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase)
  {
    override def toJson: Value = super.toJson
    def getSubPhaseName: String = "collectTaxes"
}
