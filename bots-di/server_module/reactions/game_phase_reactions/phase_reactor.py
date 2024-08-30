from DTO.phases.all_phases import SubPhase
from server_module.reactions.game_phase_reactions.action.choose_house_card_reaction import ChooseHouseCardReaction
from server_module.reactions.game_phase_reactions.action.choose_to_use_valyrian_steel_blade_reaction import \
    ChooseToUseValyrianSteelBladeReaction
from server_module.reactions.game_phase_reactions.action.clean_up_after_round_reaction import CleanUpAfterRoundReaction
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
from server_module.reactions.game_phase_reactions.round_events.tracks_bids_reaction import TracksBidsReaction
from server_module.reactions.game_phase_reactions.round_events.wildlings_bids_reaction import WildlingsBidsReaction
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
    'chooseTracksBidsOrCollectTaxes': RavenChooseTrackBidsOrCollectTaxesReaction,
    'recalculateSupplies': NoActionNeededReaction,
    'tracksBids': TracksBidsReaction,
    'wildlingsBids': WildlingsBidsReaction,
    'resolveTiesAfterBiddingOnTracks': ResolveTiesAfterBiddingOnTracksReaction,
}

def react_to_phase(game_id: str, phase: SubPhase):
    try:
        PhaseReact.react(switcher_obj[phase['subPhase']], game_id, phase)
    except KeyError as e:
        print('Unimplemented phase {}'.format(phase['subPhase']))
        print_file_lineno_error(e)

        raise e
