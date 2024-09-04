package fwc.game.actions

import fwc.JsonSerializable
import fwc.game.GameState
import fwc.game.actions.action.*
import fwc.game.actions.planning.*
import fwc.game.actions.roundEvents.*


trait Action(gameState: GameState) extends JsonSerializable {
  def doAction(): GameState
  def undoAction(): GameState = gameState
}

object Action extends JsonParsableAction {
  val actionTypeJsonKey = "actionType"

  //todo refactor the rest of cases
  enum PlanningActions(val string: String) {
    case AddOrder extends PlanningActions("addOrder")
    case RemoveOrder extends PlanningActions("removeOrder")
    case OpenOrders extends PlanningActions("openOrders")
  }

  def fromJson(gameState: GameState, json: ujson.Value): Action = {
    json(actionTypeJsonKey).str match
      case PlanningActions.AddOrder.string => ActionAddOrder.fromJson(gameState, json)
      case PlanningActions.RemoveOrder.string => ActionRemoveOrder.fromJson(gameState, json)
      case PlanningActions.OpenOrders.string => ActionOpenOrders.fromJson(gameState, json)
      case "ravenChooseChangeOrderOrLookAtWildlingCard" => ActionRavenChooseChangeOrderOrLookAtWildlingCard.fromJson(gameState, json)
      case "ravenChangeOrder" => ActionRavenChangeOrder.fromJson(gameState, json)
      case "ravenChoosePutWildlingsCardOnTopOrBottom" => ActionRavenChoosePutWildlingsCardOnTopOrBottom.fromJson(gameState, json)
      case "ravenGetWildlingsCard" => ActionRavenGetWildlingsCard.fromJson(gameState, json)

      case "resolveRaidOrder" => ActionResolveRaidOrder.fromJson(gameState, json)
      case "resolveMarchOrder" => ActionResolveMarchOrder.fromJson(gameState, json)
      case "leavePowerTokenAtTile" => ActionLeavePowerTokenAtTile.fromJson(gameState, json)
      case "resolveSupportOrder" => ActionResolveSupportOrder.fromJson(gameState, json)
      case "chooseHouseCard" => ActionChooseHouseCard.fromJson(gameState, json)
      case "resolveCardPufferfish0" => ActionResolveCardPufferfish0.fromJson(gameState, json)
      case "resolveCardKraken6" => ActionResolveCardKraken6.fromJson(gameState, json)
      case "resolveCardRose2" => ActionResolveCardRose2.fromJson(gameState, json)
      case "resolveCardRose4" => ActionResolveCardRose4.fromJson(gameState, json)
      case "resolveCardLion5" => ActionResolveCardLion5.fromJson(gameState, json)
      case "getTidesOfBattleCards" => ActionGetTidesOfBattleCards.fromJson(gameState, json)
      case "setTidesOfBattleCards" => ActionSetTidesOfBattleCards.fromJson(gameState, json)
      case "refreshTidesOfBattleDeck" => ActionRefreshTidesOfBattleDeck.fromJson(gameState, json)
      case "useValyrianSteelBlade" => ActionUseValyrianSteelBlade.fromJson(gameState, json)
      case "calculateCombatOutcome" => ActionCalculateCombatOutcome.fromJson(gameState, json)
      case "resolveCardWolf0" => ActionResolveCardWolf0.fromJson(gameState, json)
      case "killUnitsAfterBattle" => ActionKillUnitsAfterBattle.fromJson(gameState, json)
      case "retreatUnitsAfterBattle" => ActionRetreatUnitsAfterBattle.fromJson(gameState, json)
      case "disbandUnitsAfterCombat" => ActionDisbandUnitsAfterCombat.fromJson(gameState, json)
      case "cleanUpAfterCombat" => ActionCleanUpAfterCombat.fromJson(gameState, json)
      case "resolveCardMoose2" => ActionResolveCardMoose2.fromJson(gameState, json)
      case "resolveCardLion1" => ActionResolveCardLion1.fromJson(gameState, json)
      case "chooseHouseCardAfterLion5" => ActionChooseHouseCardAfterLion5.fromJson(gameState, json)
      case "resolveCardMoose3" => ActionResolveCardMoose3.fromJson(gameState, json)
      case "resolveSpecialConsolidatePower" => ActionResolveSpecialConsolidatePower.fromJson(gameState, json)
      case "resolveConsolidatePowerOrder" => ActionResolveConsolidatePowerOrder.fromJson(gameState, json)
      case "cleanUpAfterRound" => ActionCleanUpAfterRound.fromJson(gameState, json)

      case "calculateGameWinner" => ActionCalculateGameWinner.fromJson(gameState, json)

      case "getEventCards" => ActionGetEventCards.fromJson(gameState, json)
      case "setEventCards" => ActionSetEventCards.fromJson(gameState, json)
      case "getWildlingsCard" => ActionGetWildlingsCard.fromJson(gameState, json)
      case "setWildlingsCard" => ActionSetWildlingsCard.fromJson(gameState, json)
      case "recalculateSupplies" => ActionRecalculateSupplies.fromJson(gameState, json)
      case "disbandUnitDueToSupplies" => ActionDisbandUnitDueToSupplies.fromJson(gameState, json)
      case "muster" => ActionMuster.fromJson(gameState, json)
      case "finishMustering" => ActionFinishMustering.fromJson(gameState, json)
      case "stopMustering" => ActionStopMustering.fromJson(gameState, json)
      case "throneChooseSupplyOrMuster" => ActionThroneChooseSupplyOrMuster.fromJson(gameState, json)
      case "collectTaxes" => ActionCollectTaxes.fromJson(gameState, json)
      case "ravenChooseTrackBidsOrCollectTaxes" => ActionRavenChooseTrackBidsOrCollectTaxes.fromJson(gameState, json)
      case "trackBids" => ActionTrackBids.fromJson(gameState, json)
      case "openTrackBids" => ActionOpenTrackBids.fromJson(gameState, json)
      case "resolveTiesAfterBiddingOnTracks" => ActionResolveTiesAfterBiddingOnTracks.fromJson(gameState, json)
      case "disableOrder" => ActionDisableOrder.fromJson(gameState, json)
      case "steelBladeChooseDisableMarchOrDefend" => ActionSteelBladeChooseDisableMarchOrDefend.fromJson(gameState, json)
      case "wildlingsBids" => ActionWildlingsBids.fromJson(gameState, json)
      case "resolveTiesAfterBiddingOnWildlings" => ActionResolveTiesAfterBiddingOnWildlings.fromJson(gameState, json)
      case "wildlingsKillUnit" => ActionWildlingsKillUnit.fromJson(gameState, json)
      case "wildlingsChooseKill2UnitsOr2PositionsOnTrack" => ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack.fromJson(gameState, json)
      case "wildlingsDiscardHouseCard" => ActionWildlingsDiscardHouseCard.fromJson(gameState, json)
      case "wildlingsReturnHouseCard" => ActionWildlingsReturnHouseCard.fromJson(gameState, json)
      case "wildlingsCard" => ActionWildlingsCard.fromJson(gameState, json)
      case "wildlingsMusterAtCastle" => ActionWildlingsMusterAtCastle.fromJson(gameState, json)
      case "wildlingsDowngradeKnights" => ActionWildlingsDowngradeKnights.fromJson(gameState, json)
      case "wildlingsUpgradeKnights" => ActionWildlingsUpgradeKnights.fromJson(gameState, json)
      case "wildlingsChooseTrackToBeFirstAt" => ActionWildlingsChooseTrackToBeFirstAt.fromJson(gameState, json)
      case "wildlingsChooseTrackToBeLastAt" => ActionWildlingsChooseTrackToBeLastAt.fromJson(gameState, json)
      case "autoKillUnitsAfterBattle" => ActionAutoKillUnitsAfterBattle.fromJson(gameState, json)
      case "autoRetreatAfterBattle" => ActionAutoRetreatAfterBattle.fromJson(gameState, json)

  }
}
