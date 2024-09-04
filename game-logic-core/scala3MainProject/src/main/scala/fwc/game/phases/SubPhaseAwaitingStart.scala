package fwc.game.phases

import fwc.game.houses.HouseType
import fwc.game.phases.MainPhase.Planning

case class SubPhaseAwaitingStart(
                                  mainPhase: MainPhase = Planning
                                )
  extends SubPhasePassiveMultipleHouses(HouseType.getSeqOfAll ,mainPhase)
  with SubPhasePassive:
  override def getSubPhaseName: String = "awaitingStart"
