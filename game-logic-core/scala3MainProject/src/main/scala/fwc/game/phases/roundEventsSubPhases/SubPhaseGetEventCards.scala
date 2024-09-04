package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseGetEventCards(
                                houseTypes: Seq[HouseType],
                                mainPhase: MainPhase = MainPhase.RoundEvents
                                )
 extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
   with SubPhaseRandom
   with SubPhasePassive {
  override def toJson: Value = super.toJson
  def getSubPhaseName: String = "getEventCards"

}
