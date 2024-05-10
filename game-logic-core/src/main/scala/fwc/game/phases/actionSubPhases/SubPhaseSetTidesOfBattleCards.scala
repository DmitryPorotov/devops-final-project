package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, PhaseAction, SubPhase, SubPhaseNoHouse, SubPhaseSingleHouse}
import fwc.gameLoading.TidesOfBattleCard
import ujson.Value

case class SubPhaseSetTidesOfBattleCards(
                                          attackerCard: Option[Int] = None,
                                          defenderCard: Option[Int] = None,
                                          mainPhase: MainPhase = PhaseAction
                                        )extends SubPhase(mainPhase) with SubPhaseNoHouse (
    mainPhase
) {
  def getSubPhaseName: String = "setTidesOfBattleCards"

  override def toJson: Value =
    val json = super.toJson
    if attackerCard.nonEmpty
    then json.obj.addAll(Map(
      "attackerCard" -> (if attackerCard.nonEmpty then attackerCard.head else ujson.Null),
      "defenderCard" -> (if defenderCard.nonEmpty then defenderCard.head else ujson.Null)
    ))
    json
}
