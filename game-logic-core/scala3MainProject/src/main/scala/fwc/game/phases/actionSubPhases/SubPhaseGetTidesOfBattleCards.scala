package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhasePassive, SubPhasePassiveMultipleHouses, SubPhaseRandom}

case class SubPhaseGetTidesOfBattleCards(
                                          houseTypes: Seq[HouseType],
                                          mainPhase: MainPhase = MainPhase.Action
                                        )
 extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
   with SubPhaseRandom
   with SubPhasePassive {
  def getSubPhaseName: String = "getTidesOfBattleCards"

}
