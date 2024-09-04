package fwc.game.phases.roundEventsSubPhases

import fwc.JsonSerializable
import fwc.game.houses.HouseType
import fwc.game.phases.*
import ujson.Value

case class SubPhaseWildlingsCard(
                                  houseTypes: Seq[HouseType],
                                  loserWinnerHouse: HouseType,
                                  cardCode: Int,
                                  isWin: Boolean,
                                  mainPhase: MainPhase = MainPhase.RoundEvents
                               )
  extends SubPhasePassiveMultipleHouses(houseTypes, mainPhase)
    with SubPhasePassive {
  def getSubPhaseName: String = "wildlingsCard"

  override def toJson: Value = {
    val json = super.toJson
    json.obj.addAll(Map(
      "loserWinnerHouse" -> loserWinnerHouse.toString,
      "cardCode" -> cardCode,
      "isWin" -> isWin,
    ))
    json
  }
}
