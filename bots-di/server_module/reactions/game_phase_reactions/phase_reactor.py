from DTO.phases.all_phases import SubPhase
from server_module.reactions.game_phase_reactions.action.auto_kill_units_after_battle_reaction import \
    AutoKillUnitsAfterBattleReaction
from server_module.reactions.game_phase_reactions.action.calculate_combat_outcome_reaction import \
    CalculateCombatOutcomeReaction
from server_module.reactions.game_phase_reactions.action.choose_house_card_reaction import ChooseHouseCardReaction
from server_module.reactions.game_phase_reactions.action.choose_to_use_valyrian_steel_blade_reaction import \
    ChooseToUseValyrianSteelBladeReaction
from server_module.reactions.game_phase_reactions.action.clean_up_after_combat_reaction import \
    CleanUpAfterCombatReaction
from server_module.reactions.game_phase_reactions.action.clean_up_after_round_reaction import CleanUpAfterRoundReaction
from server_module.reactions.game_phase_reactions.action.get_tides_of_battle_cards_reaction import \
    GetTidesOfBattleCardsReaction
from server_module.reactions.game_phase_reactions.action.leave_power_token_at_tile_reaction import \
    LeavePowerTokenAtTileReaction
from server_module.reactions.game_phase_reactions.action.resolve_consolidate_power_order_reaction import \
    ResolveConsolidatePowerOrderReaction
from server_module.reactions.game_phase_reactions.action.resolve_house_card_reaction import ResolveHouseCardReaction
from server_module.reactions.game_phase_reactions.action.resolve_march_order_reaction import ResolveMarchOrderReaction
from server_module.reactions.game_phase_reactions.action.resolve_raid_order_reaction import ResolveRaidOrderReaction
from server_module.reactions.game_phase_reactions.action.resolve_special_consolidate_power_reaction import \
    ResolveSpecialConsolidatePowerReaction
from server_module.reactions.game_phase_reactions.action.resolve_support_order_reaction import \
    ResolveSupportOrderReaction
from server_module.reactions.game_phase_reactions.action.retreat_units_after_battle_reaction import \
    RetreatUnitsAfterBattleReaction
from server_module.reactions.game_phase_reactions.action.set_tides_of_battle_cards_reaction import \
    SetTidesOfBattleCardsReaction
from server_module.reactions.game_phase_reactions.phase_react import PhaseReact
from server_module.reactions.game_phase_reactions.planning.add_order_reaction import AddOrderReaction
from server_module.reactions.game_phase_reactions.planning.raven_change_order_reaction import RavenChangeOrderReaction
from server_module.reactions.game_phase_reactions.planning.raven_choose_change_order_or_look_at_wildling_card_reaction import \
    RavenChooseChangeOrderOrLookAtWildlingCardReaction
from server_module.reactions.game_phase_reactions.planning.raven_choose_put_wildlings_card_on_top_or_bottom_reaction import \
    RavenChoosePutWildlingsCardOnTopOrBottomReaction
from server_module.reactions.game_phase_reactions.planning.raven_get_wildlings_card_reaction import \
    RavenGetWildlingsCardReaction
from utils_ import print_file_lineno_error

switcher_obj = {
    'addOrder':AddOrderReaction,
    'ravenChooseChangeOrderOrLookAtWildlingCard': RavenChooseChangeOrderOrLookAtWildlingCardReaction,
    'ravenGetWildlingsCard': RavenGetWildlingsCardReaction,
    'ravenChoosePutWildlingsCardOnTopOrBottom': RavenChoosePutWildlingsCardOnTopOrBottomReaction,
    'ravenChangeOrder': RavenChangeOrderReaction,
    'resolveRaidOrder': ResolveRaidOrderReaction,
    'resolveMarchOrder': ResolveMarchOrderReaction,
    'leavePowerTokenAtTile': LeavePowerTokenAtTileReaction,
    'resolveConsolidatePowerOrder': ResolveConsolidatePowerOrderReaction,
    'resolveSpecialConsolidatePower': ResolveSpecialConsolidatePowerReaction,
    'chooseHouseCard': ChooseHouseCardReaction,
    'resolveSupportOrder': ResolveSupportOrderReaction,
    'getTidesOfBattleCards': GetTidesOfBattleCardsReaction,
    'setTidesOfBattleCards': SetTidesOfBattleCardsReaction,
    'resolveHouseCard': ResolveHouseCardReaction,
    'chooseToUseValyrianSteelBlade': ChooseToUseValyrianSteelBladeReaction,
    'cleanUpAfterRound': CleanUpAfterRoundReaction,
    'calculateCombatOutcome': CalculateCombatOutcomeReaction,
    'retreatUnitsAfterBattle': RetreatUnitsAfterBattleReaction,
    'autoKillUnitsAfterBattle': AutoKillUnitsAfterBattleReaction,
    'cleanUpAfterCombat': CleanUpAfterCombatReaction
}

def react_to_phase(game_id: str, phase: SubPhase):
    try:
        PhaseReact.react(switcher_obj[phase['subPhase']], game_id, phase)
    except KeyError as e:
        print_file_lineno_error(e)

        raise e
