package generator

import fwc.communication.messagesFromClient.*
import fwc.{JsonSerializable, Player}
import fwc.communication.reactions.ReactionCreateGame
import fwc.communication.repliesToClient.*
import fwc.game.actionPhase.{RavenChoiceNothing, ValyrianSteelBladeChoiceNothing}
import fwc.game.board.{MilitaryUnit, MilitaryUnitKnights, TrackThrone, TrackType}
import fwc.gameSaving.actions.Action
import fwc.gameSaving.actions.planning.*
import fwc.game.planningPhase.{Order, OrderMarch}
import fwc.game.houses.*
import fwc.gameSaving.actions.action.*
import fwc.gameSaving.actions.roundEvents.*
import fwc.gameLoading.*
import fwc.game.eventsPhase.cards.EventCardChoiceA
import fwc.game.gameRules
import fwc.game.phases.{MainPhase, SubPhase, SubPhaseAwaitingStart}
import fwc.game.phases.SubPhase.{getFieldsOfMultipleHouses, getFieldsOfMultipleHousesTracks, getFieldsOfNoHouse, getFieldsOfSingleHouse, getFieldsOfWildlingsMultipleHouses}
import fwc.game.phases.actionSubPhases.*
import fwc.game.phases.planningSubPhases.*
import fwc.game.phases.roundEventsSubPhases.*
import fwc.game.planningPhase.*
import fwc.gameSaving.GameReplay

import scala.util.Try

object InstanciesCollection {
  val actions: Map[String, Map[String, Action]] = Map(
    "planning" -> Map(
      "ActionAddOrder" -> ActionAddOrder(null, HouseLion, Order(OrderMarch), 3),
      "ActionRemoveOrder" -> ActionRemoveOrder(null, HouseLion, 3),
      "ActionOpenOrders" -> ActionOpenOrders(null, HouseLion),
      "ActionRavenChooseChangeOrderOrLookAtWildlingCard" -> ActionRavenChooseChangeOrderOrLookAtWildlingCard(null, HouseLion, RavenChoiceNothing),
      "ActionRavenChangeOrder" -> ActionRavenChangeOrder(null, HouseLion, Order(OrderMarch), 3),
      "ActionRavenChoosePutWildlingsCardOnTopOrBottom" -> ActionRavenChoosePutWildlingsCardOnTopOrBottom(null, HouseLion, true),
      "ActionRavenGetWildlingsCard" -> ActionRavenGetWildlingsCard(null, false),
    ),
    "action" -> Map(
      "ActionResolveRaidOrder" -> ActionResolveRaidOrder(null, HouseLion, 1, 2),
      "ActionResolveMarchOrder" -> ActionResolveMarchOrder(null, HouseLion, 1, Map(2 -> Seq(MilitaryUnit(HouseLion, MilitaryUnitKnights)))),
      "ActionLeavePowerTokenAtTile" -> ActionLeavePowerTokenAtTile(null, HouseLion, 1, true),
      "ActionResolveSupportOrder" -> ActionResolveSupportOrder(null, HouseLion, HouseWolf, Seq(3, 4)),
      "ActionChooseHouseCard" -> ActionChooseHouseCard(null, HouseLion, 0),
      "ActionResolveCardPufferfish0" -> ActionResolveCardPufferfish0(null, HouseLion, TrackThrone),
      "ActionResolveCardKraken6" -> ActionResolveCardKraken6(null, HouseLion),
      "ActionResolveCardRose2" -> ActionResolveCardRose2(null, HouseLion),
      "ActionResolveCardRose4" -> ActionResolveCardRose4(null, HouseLion, 4),
      "ActionResolveCardLion5" -> ActionResolveCardLion5(null, HouseLion),
      "ActionGetTidesOfBattleCards" -> ActionGetTidesOfBattleCards(null, false),
      "ActionSetTidesOfBattleCards" -> ActionSetTidesOfBattleCards(null, 1, 2),
      "ActionRefreshTidesOfBattleDeck" -> ActionRefreshTidesOfBattleDeck(null, Seq(TidesOfBattleCard(1,1))),
      "ActionUseValyrianSteelBlade" -> ActionUseValyrianSteelBlade(null, HouseLion, ValyrianSteelBladeChoiceNothing),
      "ActionCalculateCombatOutcome" -> ActionCalculateCombatOutcome(null),
      "ActionResolveCardWolf0" -> ActionResolveCardWolf0(null, HouseLion, 5),
      "ActionKillUnitsAfterBattle" -> ActionKillUnitsAfterBattle(null, HouseLion, Seq(MilitaryUnit(HouseLion, MilitaryUnitKnights))),
      "ActionRetreatUnitsAfterBattle" -> ActionRetreatUnitsAfterBattle(null, HouseLion, 6),
      "ActionDisbandUnitsAfterCombat" -> ActionDisbandUnitsAfterCombat(null, HouseLion, MilitaryUnit(HouseLion, MilitaryUnitKnights)),
      "ActionCleanUpAfterCombat" -> ActionCleanUpAfterCombat(null),
      "ActionResolveCardMoose2" -> ActionResolveCardMoose2(null, HouseLion, 1),
      "ActionResolveCardLion1" -> ActionResolveCardLion1(null, HouseLion, 1),
      "ActionChooseHouseCardAfterLion5" -> ActionChooseHouseCardAfterLion5(null, HouseLion, 1),
      "ActionResolveCardMoose3" -> ActionResolveCardMoose3(null, HouseLion, 1),
      "ActionResolveSpecialConsolidatePower" -> ActionResolveSpecialConsolidatePower(null, HouseLion, 1, MilitaryUnit(HouseLion, MilitaryUnitKnights)),
      "ActionResolveConsolidatePowerOrder" -> ActionResolveConsolidatePowerOrder(null),
      "ActionCleanUpAfterRound" -> ActionCleanUpAfterRound(null, false),
      "ActionCalculateGameWinner" -> ActionCalculateGameWinner(null),
    ),
    "events" -> Map(
      "ActionGetEventCards" -> ActionGetEventCards(null, false),
      "ActionSetEventCards" -> ActionSetEventCards(null, RoundEventCard(1, "", "", 0), RoundEventCard(1, "", "", 0), RoundEventCard(1, "", "", 0)),
      "ActionGetWildlingsCard" -> ActionGetWildlingsCard(null, false),
      "ActionSetWildlingsCard" -> ActionSetWildlingsCard(null, 1),
      "ActionRecalculateSupplies" -> ActionRecalculateSupplies(null),
      "ActionDisbandUnitDueToSupplies" -> ActionDisbandUnitDueToSupplies(null, HouseLion, 1, MilitaryUnit(HouseLion, MilitaryUnitKnights), UnitDisbandNextStepDeck1),
      "ActionMuster" -> ActionMuster(null, HouseLion, MilitaryUnit(HouseLion, MilitaryUnitKnights), 1, Some(2), false),
      "ActionStopMustering" -> ActionStopMustering(null, HouseLion),
      "ActionThroneChooseSupplyOrMuster" -> ActionThroneChooseSupplyOrMuster(null, HouseLion, EventCardChoiceA),
      "ActionCollectTaxes" -> ActionCollectTaxes(null),
      "ActionRavenChooseTrackBidsOrCollectTaxes" -> ActionRavenChooseTrackBidsOrCollectTaxes(null, HouseLion, EventCardChoiceA),
      "ActionTrackBids" -> ActionTrackBids(null, HouseLion, 1),
      "ActionResolveTiesAfterBiddingOnTracks" -> ActionResolveTiesAfterBiddingOnTracks(null, HouseLion, Seq(HouseWolf, HouseRose)),
      "ActionDisableOrder" -> ActionDisableOrder(null, OrderDefend),
      "ActionSteelBladeChooseDisableMarchOrDefend" -> ActionSteelBladeChooseDisableMarchOrDefend(null, HouseLion, EventCardChoiceA),
      "ActionWildlingsBids" -> ActionWildlingsBids(null, HouseLion, 1),
      "ActionResolveTiesAfterBiddingOnWildlings" -> ActionResolveTiesAfterBiddingOnWildlings(null, HouseLion, HouseWolf),
      "ActionWildlingsKillUnit" -> ActionWildlingsKillUnit(null, HouseLion, 1, MilitaryUnit(HouseLion, MilitaryUnitKnights)),
      "ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack" -> ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack(null, HouseLion, Some(TrackThrone), None),
      "ActionWildlingsDiscardHouseCard" -> ActionWildlingsDiscardHouseCard(null, HouseLion, 1),
      "ActionWildlingsReturnHouseCard" -> ActionWildlingsReturnHouseCard(null, HouseLion, None),
      "ActionWildlingsCard" -> ActionWildlingsCard(null),
      "ActionWildlingsMusterAtCastle" -> ActionWildlingsMusterAtCastle(null, HouseLion, 3, Seq()),
      "ActionWildlingsDowngradeKnights" -> ActionWildlingsDowngradeKnights(null, HouseLion, 1),
      "ActionWildlingsUpgradeKnights" -> ActionWildlingsUpgradeKnights(null, HouseLion, 1, Some(2)),
      "ActionWildlingsChooseTrackToBeFirstAt" -> ActionWildlingsChooseTrackToBeFirstAt(null, HouseLion, TrackThrone),
      "ActionWildlingsChooseTrackToBeLastAt" -> ActionWildlingsChooseTrackToBeLastAt(null, HouseLion, TrackThrone),
    ),
  )
  val phases:  Map[String, SubPhase] = Map(
   "SubPhaseRavenChangeOrder" ->  SubPhaseRavenChangeOrder(HouseLion),
   "SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard" ->  SubPhaseRavenChooseChangeOrderOrLookAtWildlingCard(HouseLion),
   "SubPhaseChooseHouseCard" ->  SubPhaseChooseHouseCard(Seq(HouseLion, HouseWolf)),
   "SubPhaseChooseHouseCardAfterLion5" ->  SubPhaseChooseHouseCardAfterLion5(HouseLion, 5),
   "SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom" ->  SubPhaseRavenChoosePutWildlingsCardOnTopOrBottom(HouseLion),
   "SubPhaseChooseToUseValyrianSteelBlade" ->  SubPhaseChooseToUseValyrianSteelBlade(HouseLion),
   "SubPhaseKillUnitsAfterBattle" ->  SubPhaseKillUnitsAfterBattle(HouseLion),
   "SubPhaseResolveHouseCard" ->  SubPhaseResolveHouseCard(HouseLion, 3),
   "SubPhaseResolveMarchOrder" ->  SubPhaseResolveMarchOrder(HouseLion),
   "SubPhaseResolveRaidOrder" ->  SubPhaseResolveRaidOrder(HouseLion),
   "SubPhaseResolveSpecialConsolidatePower" ->  SubPhaseResolveSpecialConsolidatePower(HouseLion),
   "SubPhaseResolveSupportOrder" ->  SubPhaseResolveSupportOrder(HouseLion, Seq(1,2)),
   "SubPhaseRetreatUnitsAfterBattle" ->  SubPhaseRetreatUnitsAfterBattle(HouseLion),
   "SubPhaseChooseDisableMarchPlus1OrDefendOrders" ->  SubPhaseChooseDisableMarchPlus1OrDefendOrders(HouseLion),
   "SubPhaseChooseTracksBidsOrCollectTaxes" ->  SubPhaseChooseTracksBidsOrCollectTaxes(HouseLion),
   "SubPhaseChooseUpdateSupplyOrMuster" ->  SubPhaseChooseUpdateSupplyOrMuster(HouseLion),
   "SubPhaseDisbandUnit" ->  SubPhaseDisbandUnit(HouseLion, UnitDisbandNextStepDeck2),
   "SubPhaseMuster" ->  SubPhaseMuster(HouseLion),
   "SubPhaseSetTidesOfBattleCards" ->  SubPhaseSetTidesOfBattleCards(Some(1), Some(2)),
   "SubPhaseSetEventCards" ->  SubPhaseSetEventCards(Some(RoundEventCard(1, "", "", 0)), Some(RoundEventCard(1, "", "", 0)), Some(RoundEventCard(1, "", "", 0))),
   "SubPhaseSetWildlingsCards" ->  SubPhaseSetWildlingsCard(SubPhaseWildlingsCard(Seq(HouseLion, HouseWolf), HouseLion, 1, true)),
   "SubPhaseAddOrder" ->  SubPhaseAddOrder(Seq(HouseLion, HouseWolf)),
   "SubPhaseReadyToOpenOrders" ->  SubPhaseReadyToOpenOrders(Seq(HouseLion, HouseWolf)),
   "SubPhaseWildlingsBids" ->  SubPhaseWildlingsBids(Seq(HouseLion, HouseWolf), 6, true),
   "SubPhaseWildlingsCard" ->  SubPhaseWildlingsCard(Seq(HouseLion, HouseWolf), HouseLion, 1, true),
   "SubPhaseTracksBids" ->  SubPhaseTracksBids(Seq(HouseLion, HouseWolf), TrackThrone),
   "SubPhaseResolveTiesAfterBiddingOnTracks" ->  SubPhaseResolveTiesAfterBiddingOnTracks(HouseLion, TrackThrone),
   "SubPhaseCleanUpAfterRound" ->  SubPhaseCleanUpAfterRound(),
   "SubPhaseLeavePowerTokenAtTile" ->  SubPhaseLeavePowerTokenAtTile(HouseLion, 1),
   "SubPhaseCalculateCombatOutcome" ->  SubPhaseCalculateCombatOutcome(),
   "SubPhaseCleanUpAfterCombat" ->  SubPhaseCleanUpAfterCombat(),
   "SubPhaseCalculateGameWinner" ->  SubPhaseCalculateGameWinner(),
   "SubPhaseRecalculateSupplies" ->  SubPhaseRecalculateSupplies(),
   "SubPhaseCollectTaxes" ->  SubPhaseCollectTaxes(),
   "SubPhaseDisableOrder" ->  SubPhaseDisableOrder(OrderMarch),
   "SubPhaseResolveTiesAfterBiddingOnWildlings" ->  SubPhaseResolveTiesAfterBiddingOnWildlings(Seq(HouseLion, HouseWolf), true),
   "SubPhaseWildlingsDiscardHouseCard" ->  SubPhaseWildlingsDiscardHouseCard(Seq(HouseLion, HouseWolf)),
   "SubPhaseWildlingsKillUnits" ->  SubPhaseWildlingsKillUnits(Map(HouseLion -> 1, HouseWolf -> 2), Some(HouseWolf)),
   "SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack" ->  SubPhaseWildlingsChooseKill2UnitsOr2PositionsOnTrack(HouseLion),
   "SubPhaseWildlingsMusterAtCastle" ->  SubPhaseWildlingsMusterAtCastle(HouseLion),
   "SubPhaseWildlingsDowngradeKnights" ->  SubPhaseWildlingsDowngradeKnights(Map(HouseLion -> 1, HouseWolf -> 2)),
   "SubPhaseWildlingsUpgradeKnights" ->  SubPhaseWildlingsUpgradeKnights(HouseLion),
   "SubPhaseWildlingsChooseTrackToBeFirstAt" ->  SubPhaseWildlingsChooseTrackToBeFirstAt(HouseLion),
   "SubPhaseWildlingsChooseTrackToBeLastAt" ->  SubPhaseWildlingsChooseTrackToBeLastAt(Seq(HouseLion, HouseWolf)),
   "SubPhaseRefreshTidesOfBattleDeck" ->  SubPhaseRefreshTidesOfBattleDeck(),
   "SubPhaseResolveConsolidatePowerOrder" ->  SubPhaseResolveConsolidatePowerOrder(),
   "SubPhaseRavenGetWildlingsCard" ->  SubPhaseRavenGetWildlingsCard(),
   "SubPhaseGetTidesOfBattleCards" ->  SubPhaseGetTidesOfBattleCards(),
   "SubPhaseGetEventCards" ->  SubPhaseGetEventCards(),
   "SubPhaseGetWildlingsCard" ->  SubPhaseGetWildlingsCard(SubPhaseWildlingsCard(Seq(HouseLion, HouseWolf), HouseLion, 1, true)),
   "SubPhaseAwaitingStart" ->  SubPhaseAwaitingStart(),
   "SubPhaseAutoKillUnitsAfterBattle" ->  SubPhaseAutoKillUnitsAfterBattle(),
   "SubPhaseAutoRetreatAfterBattle" ->  SubPhaseAutoRetreatAfterBattle(),
  )
  val replies: Map[String, JsonSerializable] = {
    val (id, settings, state) = ReactionCreateGame(1, "2", false, false)
    val replay = GameReplay(settings, state.boardCards, state, Seq())
    val statusDetails = StatusDetails(replay)
    Map(
      "ReplyCreateGame" -> ReplyCreateGame(id, "dsafsdfasfs"),
      "ReplyError" -> ReplyError(1, id, "get fucked", ujson.Obj("action" -> "hello"), "fsfsfsasdfs"),
      "ReplyGameAction" -> ReplyGameAction(id, ujson.Obj(), "fsafsdfsd"),
      "ReplyGetGameState" -> {
        ReplyGetGameState(1, id, gameRules, state, settings, "sadf")
      },
      "ReplyGetStatus" -> ReplyGetStatus(1, id, replay, "sdfasfs"),
      "ReplyJoinGame" -> ReplyJoinGame(1, id, settings.copy(players = Some(Seq(
        Player(
          1, "player name", Some(HouseLion)
        )
      ))), "dsasdfsd"),
      "ReplyListSaves" -> ReplyListSaves(1, id, Seq("save1.json", "save2.json") , "dsfasdfdsf"),
      "ReplyLoadGame" -> ReplyLoadGame(id, "dsafsdafgsd"),
      "ReplyNewGame" -> ReplyNewGame(id, 3, "sfsadfsdf"),
      "ReplySaveGame" -> ReplySaveGame(1, id, "save5.json", "dsafdsffd"),
      "ReplyStartGame" -> ReplyStartGame(id, "sadfdsafsdfewgrhytu34"),
      "ReplyTestConnectivity" -> ReplyTestConnectivity("safdsdhthew"),
      "GameStatus" -> GameStatus(true, statusDetails.toJson),
      "StatusDetails" -> statusDetails,
    )
  }
  
  val messages: Map[String, JsonSerializable] = Map(
    "Message" -> new Message(1, "2", "safsdfasdfads"),
    "MessageGameAction" -> MessageGameAction(1, "2", ujson.Obj(), "fsfdsfasf")
  )
}
