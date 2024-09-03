package fwc.game.phases

import fwc.game.actions.roundEvents.UnitDisbandNextStepType
import fwc.game.board.TrackType
import fwc.game.gameRules
import fwc.game.houses.HouseType
import fwc.{JsonParsable, JsonSerializable}
import fwc.game.phases.actionSubPhases.*
import fwc.game.phases.planningSubPhases.*
import fwc.game.phases.roundEventsSubPhases.*
import fwc.game.planningPhase.OrderType
import ujson.Value

import scala.util.Try

trait SubPhase(mainPhase: MainPhase) extends JsonSerializable {
  def getSubPhaseName: String
  
  def getMainPhase: MainPhase = mainPhase
}

object SubPhase extends JsonParsable {
  override def fromJson(json: Value): SubPhase = {
    val subPhase = json.obj("subPhase").str
    subPhase match
      case "ravenChangeOrder" =>
        val (houseType: HouseType, mainPhase: MainPhase) = getFieldsOfSingleHouse(json)
        SubPhaseRavenChangeOrder(houseType, mainPhase)
      case "ravenChooseChangeOrderOrLookAtWildlingCard" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard(f._1, f._2)
      case "chooseHouseCard" =>
        val f = getFieldsOfMultipleHouses(json)
        SubPhaseChooseHouseCard(f._1, f._2)
      case "chooseHouseCardAfterLion5" =>
        val bannedCardCode = json("bannedCardCode").num.toInt
        val (houseType: HouseType, mainPhase: MainPhase) = getFieldsOfSingleHouse(json)
        SubPhaseChooseHouseCardAfterLion5(houseType, bannedCardCode, mainPhase)
      case "ravenChoosePutWildlingsCardOnTopOrBottom" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(f._1, f._2)
      case "chooseToUseValyrianSteelBlade" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseChooseToUseValyrianSteelBlade(f._1, f._2)
      case "killUnitsAfterBattle" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseKillUnitsAfterBattle(f._1, f._2)
      case "resolveHouseCard" =>
        val f = getFieldsOfSingleHouse(json)
        val cardCode = json("cardCode").num.toInt
        SubPhaseResolveHouseCard(f._1, cardCode, f._2)
      case "resolveCardRose2" =>
        val (ht, mp) = getFieldsOfSingleHouse(json)
        SubPhaseResolveCardRose2(ht, mp)
      case "resolveMarchOrder" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseResolveMarchOrder(f._1, f._2)
      case "resolveRaidOrder" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseResolveRaidOrder(f._1, f._2)
      case "resolveSpecialConsolidatePower" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseResolveSpecialConsolidatePower(f._1, f._2)
      case "resolveSupportOrder" =>
        val f = getFieldsOfSingleHouse(json)
        val tileNumbers = json("tilesNumbers").arr.map(_.num.toInt).toSeq
        SubPhaseResolveSupportOrder(f._1, tileNumbers, f._2)
      case "retreatUnitsAfterBattle" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseRetreatUnitsAfterBattle(f._1, f._2)
      case "chooseDisableMarchPlus1OrDefendOrders" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseChooseDisableMarchPlus1OrDefendOrders(f._1, f._2)
      case "chooseTracksBidsOrCollectTaxes" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseChooseTracksBidsOrCollectTaxes(f._1, f._2)
      case "chooseUpdateSupplyOrMuster" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseChooseUpdateSupplyOrMuster(f._1, f._2)
      case "disbandUnit" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseDisbandUnit(f._1, UnitDisbandNextStepType.fromString(json("nextStep").str), f._2)
      case "muster" =>
        val (ht, mp) = getFieldsOfSingleHouse(json)
        SubPhaseMuster(ht, mp)
      case "setTidesOfBattleCards" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        val atkCard = Try(json("attackerCard").numOpt.map(_.toInt)).getOrElse(None)
        val defCard = Try(json("defenderCard").numOpt.map(_.toInt)).getOrElse(None)
        SubPhaseSetTidesOfBattleCards(ht, atkCard, defCard, mp)
      case "setEventCards" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        val cardCode1 = Try(json("card1").numOpt.map(c => gameRules.boardCards.roundEvents1.find(_.code == c.toInt).head)).getOrElse(None)
        val cardCode2 = Try(json("card2").numOpt.map(c => gameRules.boardCards.roundEvents2.find(_.code == c.toInt).head)).getOrElse(None)
        val cardCode3 = Try(json("card3").numOpt.map(c => gameRules.boardCards.roundEvents3.find(_.code == c.toInt).head)).getOrElse(None)
        SubPhaseSetEventCards(ht, cardCode1, cardCode2, cardCode3, mp)
      case "setWildlingsCards" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseSetWildlingsCard(
          ht,
          SubPhase.fromJson(json("subPhaseWildlingsCard")).asInstanceOf[SubPhaseWildlingsCard],
          mp
        )
      case "addOrder" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseAddOrder(ht, mp)
      case "readyToOpenOrders" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseReadyToOpenOrders(ht, mp)
      case "wildlingsBids" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseWildlingsBids(
          ht,
          json("numberOfParticipants").num.toInt,
          json("wildlingsStartedFrom12Points").bool,
          json("wildlingsCounter").num.toInt,
          mp,
        )
      case "wildlingsCard" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        val loserWinnerHouse = HouseType.fromString(json("loserWinnerHouse").str)
        val cardCode = json("cardCode").num.toInt
        val isWin = json("isWin").bool
        SubPhaseWildlingsCard(ht, loserWinnerHouse, cardCode, isWin, mp)
      case "tracksBids" =>
        val f = getFieldsOfMultipleHousesTracks(json)
        SubPhaseTracksBids(f._1, f._2, f._3)
      case "resolveTiesAfterBiddingOnTracks" =>
        val (ht, mp) = getFieldsOfSingleHouse(json)
        SubPhaseResolveTiesAfterBiddingOnTracks(ht, TrackType.fromString(json.obj("trackType").str) ,mp)
      case "cleanUpAfterRound" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseCleanUpAfterRound(ht, mp)
      case "leavePowerTokenAtTile" =>
        val f = getFieldsOfSingleHouse(json)
        val tileNumber = json("tileNumber").num.toInt
        SubPhaseLeavePowerTokenAtTile(f._1, tileNumber, f._2)
      case "calculateCombatOutcome" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseCalculateCombatOutcome(ht, mp)
      case "cleanUpAfterCombat" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseCleanUpAfterCombat(ht, mp)
      case "calculateGameWinner" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseCalculateGameWinner(ht, mp)
      case "recalculateSupplies" =>
        SubPhaseRecalculateSupplies()
      case "collectTaxes" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseCollectTaxes(ht, mp)
      case "disableOrder" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseDisableOrder(ht, OrderType.fromString(json("orderType").str), mp)
      case "resolveTiesAfterBiddingOnWildlings" =>
        val (hts, mp) = getFieldsOfMultipleHouses(json)
        val (ht, _) = getFieldsOfSingleHouse(json)
        SubPhaseResolveTiesAfterBiddingOnWildlings(ht, hts, json("isWinner").bool, mp)
      case "wildlingsDiscardHouseCard" =>
        val f = getFieldsOfMultipleHouses(json)
        SubPhaseWildlingsDiscardHouseCard(f._1, f._2)
      case "wildlingsKillUnits" =>
        val f = getFieldsOfWildlingsMultipleHouses(json)
        val loserHouse = Try[Option[HouseType]](json("loserHouse").strOpt.map(s => HouseType.fromString(s))).getOrElse(None)
        SubPhaseWildlingsKillUnits(f._1, loserHouse, f._2)
      case "wildlingsChooseKill2UnitsOr2PositionsOnTrack" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(f._1, f._2)
      case "wildlingsMusterAtCastle" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseWildlingsMusterAtCastle(f._1, f._2)
      case "wildlingsDowngradeKnights" =>
        val f = getFieldsOfWildlingsMultipleHouses(json)
        SubPhaseWildlingsDowngradeKnights(f._1, f._2)
      case "wildlingsUpgradeKnights" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseWildlingsUpgradeKnights(f._1, f._2)
      case "wildlingsChooseTrackToBeFirstAt" =>
        val f = getFieldsOfSingleHouse(json)
        SubPhaseWildlingsChooseTrackToBeFirstAt(f._1, f._2)
      case "wildlingsChooseTrackToBeLastAt" =>
        val f = getFieldsOfMultipleHouses(json)
        SubPhaseWildlingsChooseTrackToBeLastAt(f._1, f._2)
      case "refreshTidesOfBattleDeck" =>
        SubPhaseRefreshTidesOfBattleDeck()
      case "resolveConsolidatePowerOrder" =>
        SubPhaseResolveConsolidatePowerOrder()
      case "ravenGetWildlingsCard" =>
        val (ht, mp) = getFieldsOfSingleHouse(json)
        SubPhaseRavenGetWildlingsCard(ht, mp)
      case "getTidesOfBattleCards" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseGetTidesOfBattleCards(ht, mp)
      case "getEventCards" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseGetEventCards(ht, mp)
      case "getWildlingsCard" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseGetWildlingsCard(
          ht,
          SubPhase.fromJson(json("subPhaseWildlingsCard")).asInstanceOf[SubPhaseWildlingsCard],
          mp
        )
      case "awaitingStart" =>
        SubPhaseAwaitingStart()
      case "autoKillUnitsAfterBattle" =>
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseAutoKillUnitsAfterBattle(ht, mp)
      case "autoRetreatAfterBattle" =>
        val (ht, mp) = getFieldsOfSingleHouse(json)
        SubPhaseAutoRetreatAfterBattle(ht, mp)
      case "openTrackBids" =>
        val trackType = TrackType.fromString(json("trackType").str)
        val (ht, mp) = getFieldsOfMultipleHouses(json)
        SubPhaseOpenTrackBids(ht, trackType, mp)
  }


  private def getFieldsOfNoHouse(json: Value): MainPhase =
      MainPhase.stringToMainPhase(json.obj("mainPhase").str)

  private def getFieldsOfSingleHouse(json: Value): (HouseType, MainPhase) =
    (
      HouseType.fromString(json.obj("houseType").str),
      getFieldsOfNoHouse(json)
    )


  private def getFieldsOfMultipleHouses(json: Value): (Seq[HouseType], MainPhase) =
    (
      json.obj("houseTypes").arr.map(h => HouseType.fromString(h.str)).toSeq,
      getFieldsOfNoHouse(json)
    )

  private def getFieldsOfMultipleHousesTracks(json: Value): (Seq[HouseType], TrackType, MainPhase) =
    val multipleHouses = getFieldsOfMultipleHouses(json)
    (
      multipleHouses._1,
      TrackType.fromString(json.obj("trackType").str),
      multipleHouses._2
    )

  private def getFieldsOfWildlingsMultipleHouses(json: Value): (Map[HouseType, Int], MainPhase) =
    (
      json("houseTypes").obj.map(
        (k: String, v:Value) => HouseType.fromString(k) -> v.num.toInt
      ).toMap,
      getFieldsOfNoHouse(json)
    )
}
