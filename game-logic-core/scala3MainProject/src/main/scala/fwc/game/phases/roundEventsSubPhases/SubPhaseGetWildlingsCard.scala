package fwc.game.phases.roundEventsSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseMultipleHouses, SubPhasePassive, SubPhaseRandom}

case class SubPhaseGetWildlingsCard(
                                    houseTypes: Seq[HouseType],
                                     subPhaseWildlingsCard: SubPhaseWildlingsCard,
                                     mainPhase: MainPhase = PhaseAction
                                   )
 extends SubPhase(mainPhase) 
   with SubPhasePassive(mainPhase) 
   with SubPhaseMultipleHouses(houseTypes, mainPhase)
   with SubPhaseRandom {
  def getSubPhaseName: String = "getWildlingsCard"

  override def toJson: ujson.Value =
    val json = super.toJson
    json.obj.addOne(
      "subPhaseWildlingsCard" -> subPhaseWildlingsCard.toJson
    )
    json
}
