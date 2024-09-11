package fwc.game.phases.actionSubPhases

import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhasePassive, SubPhasePassiveMultipleHouses}
import ujson.Value

case class SubPhaseRefreshTidesOfBattleDeck(
                                            previousPhase: SubPhasePassiveMultipleHouses,
                                            houseTypes: Seq[HouseType] = HouseType.getSeqOfAll,
                                            mainPhase: MainPhase = MainPhase.RoundEvents
                                           )
  extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
    with SubPhasePassive {
    def getSubPhaseName: String = "refreshTidesOfBattleDeck"

    override def toJson: Value = {
      val json = super.toJson
      json.obj.addOne(
        "previousPhase" -> previousPhase.toJson
      )
    }
}
