package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseAutoKillUnitsAfterBattle(
                                             houseTypes: Seq[HouseType],
                                             mainPhase: MainPhase = MainPhase.Action
                                           ) extends SubPhase(mainPhase)
  with SubPhasePassive(mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase)
  {
    override def toJson: Value = super.toJson

    def getSubPhaseName: String = "autoKillUnitsAfterBattle"
}
