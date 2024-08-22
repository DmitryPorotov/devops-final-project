package fwc.game.phases.actionSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseMultipleHouses, SubPhasePassive, SubPhaseSingleHouse}
import fwc.gameLoading.TidesOfBattleCard
import ujson.Value

case class SubPhaseSetTidesOfBattleCards(
                                          houseTypes: Seq[HouseType],
                                          attackerCard: Option[Int] = None,
                                          defenderCard: Option[Int] = None,
                                          mainPhase: MainPhase = MainPhase.Action
                                        ) extends SubPhase(mainPhase)
  with SubPhasePassive (mainPhase)
  with SubPhaseMultipleHouses(houseTypes, mainPhase) {
  def getSubPhaseName: String = "setTidesOfBattleCards"

  def toCleanJson: Value =
    super.toJson
  
  override def toJson: Value =
    val json = super.toJson
    if attackerCard.nonEmpty
    then json.obj.addAll(Map(
      "attackerCard" -> (if attackerCard.nonEmpty then attackerCard.head else ujson.Null),
      "defenderCard" -> (if defenderCard.nonEmpty then defenderCard.head else ujson.Null)
    ))
    json
}
