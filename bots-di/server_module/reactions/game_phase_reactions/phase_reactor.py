from DTO.phases.all_phases import SubPhase
from server_module.reactions.game_phase_reactions.action.choose_house_card_reaction import ChooseHouseCardReaction
from server_module.reactions.game_phase_reactions.action.choose_to_use_valyrian_steel_blade_reaction import \
    ChooseToUseValyrianSteelBladeReaction
from server_module.reactions.game_phase_reactions.action.clean_up_after_round_reaction import CleanUpAfterRoundReaction
from server_module.reactions.game_phase_reactions.action.house_card_reactions.choose_card_after_lion5_reaction import \
    ChooseHouseCardAfterLion5Reaction
from server_module.reactions.game_phase_reactions.action.kill_units_after_battle_reaction import \
    KillUnitsAfterBattleReaction
from server_module.reactions.game_phase_reactions.action.leave_power_token_at_tile_reaction import \
    LeavePowerTokenAtTileReaction
from server_module.reactions.game_phase_reactions.action.resolve_house_card_reaction import ResolveHouseCardReaction
from server_module.reactions.game_phase_reactions.action.resolve_march_order_reaction import ResolveMarchOrderReaction
from server_module.reactions.game_phase_reactions.action.resolve_raid_order_reaction import ResolveRaidOrderReaction
from server_module.reactions.game_phase_reactions.action.resolve_special_consolidate_power_reaction import \
    ResolveSpecialConsolidatePowerReaction
from server_module.reactions.game_phase_reactions.action.resolve_support_order_reaction import \
    ResolveSupportOrderReaction
from server_module.reactions.game_phase_reactions.action.retreat_units_after_battle_reaction import \
    RetreatUnitsAfterBattleReaction
from server_module.reactions.game_phase_reactions.no_action_needed_reaction import NoActionNeededReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_choose_kill2_units_or2_positions_on_track_reaction import \
    WildlingsChooseKill2UnitsOr2PositionsOnTrackReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_choose_track_to_be_first_at_reaction import \
    WildlingsChooseTrackToBeFirstAtReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_choose_track_to_be_last_at_reaction import \
    WildlingsChooseTrackToBeLastAtReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_discard_house_card_reaction import \
    WildlingsDiscardHouseCardReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_downgrade_knights_reaction import \
    WildlingsDowngradeKnightsReaction
from server_module.reactions.game_phase_reactions.round_events.finish_mustering_reaction import FinishMusteringReaction
from server_module.reactions.game_phase_reactions.round_events.muster_reaction import MusterReaction
from server_module.reactions.game_phase_reactions.round_events.resolve_ties_after_bidding_on_tracks_reaction import \
    ResolveTiesAfterBiddingOnTracksReaction
from server_module.reactions.game_phase_reactions.round_events.choose_disable_march_plus1_or_defend_orders_reaction import \
    ChooseDisableMarchPlus1OrDefendOrdersReaction
from server_module.reactions.game_phase_reactions.phase_react import PhaseReact
from server_module.reactions.game_phase_reactions.planning.add_order_reaction import AddOrderReaction
from server_module.reactions.game_phase_reactions.planning.raven_change_order_reaction import RavenChangeOrderReaction
from server_module.reactions.game_phase_reactions.planning.raven_choose_change_order_or_look_at_wildling_card_reaction import \
    RavenChooseChangeOrderOrLookAtWildlingCardReaction
from server_module.reactions.game_phase_reactions.planning.raven_choose_put_wildlings_card_on_top_or_bottom_reaction import \
    RavenChoosePutWildlingsCardOnTopOrBottomReaction
from server_module.reactions.game_phase_reactions.round_events.choose_tracks_bids_or_collect_taxes_reaction import \
    RavenChooseTrackBidsOrCollectTaxesReaction
from server_module.reactions.game_phase_reactions.round_events.choose_update_supply_or_muster_reaction import \
    ChooseUpdateSupplyOrMusterReaction
from server_module.reactions.game_phase_reactions.round_events.resolve_ties_after_bidding_on_wildlings_reaction import \
    ResolveTiesAfterBiddingOnWildlingsReaction
from server_module.reactions.game_phase_reactions.round_events.tracks_bids_reaction import TracksBidsReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_bids_reaction import WildlingsBidsReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_kill_unit_reaction import \
    WildlingsKillUnitReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_muster_at_castle_reaction import \
    WildlingsMusterAtCastleReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_return_house_card_reaction import \
    WildlingsReturnHouseCardReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_upgrade_knights_reaction import \
    WildlingsUpgradeKnightsReaction
from utils_ import print_file_lineno_error


switcher_obj = {
    'addOrder':AddOrderReaction,
    'ravenChooseChangeOrderOrLookAtWildlingCard': RavenChooseChangeOrderOrLookAtWildlingCardReaction,
    'ravenGetWildlingsCard': NoActionNeededReaction,
    'ravenChoosePutWildlingsCardOnTopOrBottom': RavenChoosePutWildlingsCardOnTopOrBottomReaction,
    'ravenChangeOrder': RavenChangeOrderReaction,
    'resolveRaidOrder': ResolveRaidOrderReaction,
    'resolveMarchOrder': ResolveMarchOrderReaction,
    'leavePowerTokenAtTile': LeavePowerTokenAtTileReaction,
    'resolveConsolidatePowerOrder': NoActionNeededReaction,
    'resolveSpecialConsolidatePower': ResolveSpecialConsolidatePowerReaction,
    'chooseHouseCard': ChooseHouseCardReaction,
    'resolveSupportOrder': ResolveSupportOrderReaction,
    'getTidesOfBattleCards': NoActionNeededReaction,
    'setTidesOfBattleCards': NoActionNeededReaction,
    'resolveHouseCard': ResolveHouseCardReaction,
    'chooseToUseValyrianSteelBlade': ChooseToUseValyrianSteelBladeReaction,
    'cleanUpAfterRound': CleanUpAfterRoundReaction,
    'calculateCombatOutcome': NoActionNeededReaction,
    'retreatUnitsAfterBattle': RetreatUnitsAfterBattleReaction,
    'autoKillUnitsAfterBattle': NoActionNeededReaction,
    'cleanUpAfterCombat': NoActionNeededReaction,
    'autoRetreatAfterBattle': NoActionNeededReaction,
    'chooseDisableMarchPlus1OrDefendOrders': ChooseDisableMarchPlus1OrDefendOrdersReaction,
    'disableOrder': NoActionNeededReaction,
    'chooseUpdateSupplyOrMuster': ChooseUpdateSupplyOrMusterReaction,
    'chooseTracksBidsOrCollectTaxes': RavenChooseTrackBidsOrCollectTaxesReaction, # todo make the key the same as in the action (with the word raven)
    'recalculateSupplies': NoActionNeededReaction,
    'tracksBids': TracksBidsReaction,
    'resolveTiesAfterBiddingOnTracks': ResolveTiesAfterBiddingOnTracksReaction,
    'resolveTiesAfterBiddingOnWildlings': ResolveTiesAfterBiddingOnWildlingsReaction,
    'getEventCards': NoActionNeededReaction,
    'setEventCards': NoActionNeededReaction,
    'muster': MusterReaction,
    'collectTaxes': NoActionNeededReaction,
    'finishMustering': FinishMusteringReaction,
    'killUnitsAfterBattle': KillUnitsAfterBattleReaction,
    'openTrackBids': NoActionNeededReaction,
    'resolveCardRose2': NoActionNeededReaction,
    'chooseHouseCardAfterLion5': ChooseHouseCardAfterLion5Reaction,
    'getWildlingsCard': NoActionNeededReaction,
    'setWildlingsCards': NoActionNeededReaction,
    'wildlingsCard': NoActionNeededReaction,
    'wildlingsBids': WildlingsBidsReaction,
    'wildlingsChooseKill2UnitsOr2PositionsOnTrack': WildlingsChooseKill2UnitsOr2PositionsOnTrackReaction,
    'wildlingsReturnHouseCard': WildlingsReturnHouseCardReaction,
    'wildlingsDowngradeKnights': WildlingsDowngradeKnightsReaction,
    'wildlingsUpgradeKnights': WildlingsUpgradeKnightsReaction,
    'wildlingsChooseTrackToBeFirstAt': WildlingsChooseTrackToBeFirstAtReaction,
    'wildlingsChooseTrackToBeLastAt': WildlingsChooseTrackToBeLastAtReaction,
    'wildlingsDiscardHouseCard': WildlingsDiscardHouseCardReaction,
    'wildlingsKillUnit': WildlingsKillUnitReaction,
    'wildlingsMusterAtCastle': WildlingsMusterAtCastleReaction,
}

def react_to_phase(game_id: str, phase: SubPhase):
    try:
        PhaseReact.react(switcher_obj[phase['subPhase']], game_id, phase)
    except KeyError as e:
        print('Unimplemented phase {}'.format(phase['subPhase']))
        print_file_lineno_error(e)

        raise e
