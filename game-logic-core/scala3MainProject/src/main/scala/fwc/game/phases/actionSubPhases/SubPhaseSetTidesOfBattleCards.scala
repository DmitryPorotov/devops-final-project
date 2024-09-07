package fwc.game.phases.actionSubPhases

import enrichment.ExtUPickleHashMap
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhasePassive, SubPhasePassiveMultipleHouses}
import ujson.Value

case class SubPhaseSetTidesOfBattleCards(
                                          houseTypes: Seq[HouseType],
                                          attackerCard: Option[Int] = None,
                                          defenderCard: Option[Int] = None,
                                          mainPhase: MainPhase = MainPhase.Action
                                        )
  extends SubPhasePassiveMultipleHouses (houseTypes, mainPhase)
    with SubPhasePassive {
  def getSubPhaseName: String = "setTidesOfBattleCards"

  def toCleanJson: Value =
    super.toJson
  
  override def toJson: Value =
    val json = super.toJson
    if attackerCard.nonEmpty
    then json.obj.addPairs(
      "attackerCard" -> (if attackerCard.nonEmpty then attackerCard.head else ujson.Null),
      "defenderCard" -> (if defenderCard.nonEmpty then defenderCard.head else ujson.Null)
    )
    json
}
