from typing import Optional

from dependency_injector.wiring import Provide, inject

from DTO.actions.all_actions import Action
from DTO.messages.messages import MessageGameAction
from DTO.messages.reply import Reply
from containers_module import App
from events_service import EventSourcesService
from redis_service import RedisConnector
from server_module.game_state.game_state import GameState
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_action_reactions.action.resolve_card_kraken6_reaction import \
    ResolveCardKraken6Reaction
from server_module.reactions.game_action_reactions.action.calculate_combat_outcome_reaction import \
    CalculateCombatOutcomeReaction
from server_module.reactions.game_action_reactions.action.clean_up_after_combat_house_card_reaction import \
    CleanUpAfterCombatReactionHouseCard
from server_module.reactions.game_action_reactions.action.clean_up_after_combat_reaction import \
    CleanUpAfterCombatReaction
from server_module.reactions.game_action_reactions.action.leave_power_token_at_tile_reaction import \
    LeavePowerTokenAtTileReaction
from server_module.reactions.game_action_reactions.action.resolve_card_lion1_reaction import ResolveCardLion1Reaction
from server_module.reactions.game_action_reactions.action.resolve_card_moose3_reaction import ResolveCardMoose3Reaction
from server_module.reactions.game_action_reactions.action.resolve_card_pufferfish0_reaction import \
    ResolveCardPufferfish0Reaction
from server_module.reactions.game_action_reactions.action.resolve_card_rose4_reaction import ResolveCardRose4Reaction
from server_module.reactions.game_action_reactions.action.resolve_consolidate_power_order_reaction import \
    ResolveConsolidatePowerOrderReaction
from server_module.reactions.game_action_reactions.planning.raven_choose_change_order_or_look_at_wildling_card_reaction import \
    RavenChooseChangeOrderOrLookAtWildlingCardReaction
from server_module.reactions.game_action_reactions.planning.raven_choose_put_wildlings_card_on_top_or_bottom_reaction import \
    RavenChoosePutWildlingsCardOnTopOrBottomReaction
from server_module.reactions.game_action_reactions.round_events.disable_order_reaction import DisableOrderReaction
from server_module.reactions.game_action_reactions.round_events.resolve_special_consolidate_power_reaction import \
    ResolveSpecialConsolidatePowerReaction
from server_module.reactions.game_action_reactions.round_events.resolve_ties_after_bidding_on_wildlings_reaction import \
    ResolveTiesAfterBiddingOnWildlingsReaction
from server_module.reactions.game_action_reactions.round_events.collect_taxes_reaction import CollectTaxesReaction
from server_module.reactions.game_action_reactions.round_events.finish_mustering_reaction import FinishMusteringReaction
from server_module.reactions.game_action_reactions.round_events.get_wildlings_card_reaction import \
    GetWildlingsCardReaction
from server_module.reactions.game_action_reactions.round_events.recalculate_supplies_reaction import \
    RecalculateSuppliesReaction
from server_module.reactions.game_action_reactions.round_events.resolve_ties_after_bidding_on_tracks_reaction import \
    ResolveTiesAfterBiddingOnTracksReaction
from server_module.reactions.game_action_reactions.round_events.muster_reaction import MusterReaction
from server_module.reactions.game_action_reactions.action.nothing_to_update_generic_reaction import \
    NothingToUpdateGenericReaction
from server_module.reactions.game_action_reactions.action.resolve_march_order_reaction import ResolveMarchOrderReaction
from server_module.reactions.game_action_reactions.action.resolve_raid_order_reaction import ResolveRaidOrderReaction
from server_module.reactions.game_action_reactions.action.use_valyrian_steel_blade_reaction import \
    UseValyrianSteelBladeReaction
from server_module.reactions.game_action_reactions.planning.open_orders_reaction import OpenOrdersReaction
from server_module.reactions.game_action_reactions.planning.raven_change_order_reaction import RavenChangeOrderReaction
from server_module.reactions.game_action_reactions.round_events.open_track_bids_reaction import OpenTrackBidsReaction
from server_module.reactions.game_action_reactions.round_events.set_wildlings_card_reaction import \
    SetWildlingsCardReaction
from server_module.reactions.game_action_reactions.round_events.wildlings_kill_unit_reaction import \
    WildlingsKillUnitReaction
from server_module.reactions.game_action_reactions.action.clean_up_after_round_reaction import CleanUpAfterRoundReaction
from server_module.reactions.game_action_reactions.round_events.wildlings_muster_at_castle_reaction import \
    WildlingsMusterAtCastleReaction
from server_module.reactions.game_phase_reactions.phase_reactor import react_to_phase

switch_obj = {
    'ravenChangeOrder': RavenChangeOrderReaction,
    'resolveRaidOrder': ResolveRaidOrderReaction,
    'resolveMarchOrder': ResolveMarchOrderReaction,
    'leavePowerTokenAtTile': LeavePowerTokenAtTileReaction,
    'calculateCombatOutcome': CalculateCombatOutcomeReaction,
    'cleanUpAfterCombat': CleanUpAfterCombatReaction,
    'resolveCardLion1': ResolveCardLion1Reaction,
    'resolveCardMoose2': CleanUpAfterCombatReactionHouseCard,
    'resolveCardMoose3': ResolveCardMoose3Reaction,
    'resolveCardWolf0': CleanUpAfterCombatReactionHouseCard,
    'openTrackBids': OpenTrackBidsReaction,
    'useValyrianSteelBlade': UseValyrianSteelBladeReaction,
    'muster': MusterReaction,
    'resolveTiesAfterBiddingOnTracks': ResolveTiesAfterBiddingOnTracksReaction,
    'recalculateSupplies': RecalculateSuppliesReaction,
    'collectTaxes': CollectTaxesReaction,
    'wildlingsKillUnit': WildlingsKillUnitReaction,
    'cleanUpAfterRound': CleanUpAfterRoundReaction,
    'ravenChooseChangeOrderOrLookAtWildlingCard': RavenChooseChangeOrderOrLookAtWildlingCardReaction,
    'ravenChoosePutWildlingsCardOnTopOrBottom': RavenChoosePutWildlingsCardOnTopOrBottomReaction,
    'resolveConsolidatePowerOrder': ResolveConsolidatePowerOrderReaction,
    'getWildlingsCard': GetWildlingsCardReaction,
    'setWildlingsCard': SetWildlingsCardReaction, # this case is handled in the 'if' below
    'resolveCardKraken6': ResolveCardKraken6Reaction,
    'resolveSpecialConsolidatePower': ResolveSpecialConsolidatePowerReaction,
    'finishMustering': FinishMusteringReaction,
    'resolveTiesAfterBiddingOnWildlings': ResolveTiesAfterBiddingOnWildlingsReaction,
    'disableOrder': DisableOrderReaction,
    'resolveCardPufferfish0': ResolveCardPufferfish0Reaction,
    'wildlingsMusterAtCastle': WildlingsMusterAtCastleReaction,
    'resolveCardRose4': ResolveCardRose4Reaction,

    'wildlingsCard': NothingToUpdateGenericReaction, # is it always a passive server action?
    'openOrders': NothingToUpdateGenericReaction,
    'trackBids': NothingToUpdateGenericReaction,
    'wildlingsBids': NothingToUpdateGenericReaction,
    'ravenGetWildlingsCard': NothingToUpdateGenericReaction,   # todo should save it to ravens personal memory later
    'chooseHouseCard': NothingToUpdateGenericReaction,
    'getTidesOfBattleCards': NothingToUpdateGenericReaction,
    'setTidesOfBattleCards': NothingToUpdateGenericReaction,
    'addOrder': NothingToUpdateGenericReaction,
    'autoKillUnitsAfterBattle': NothingToUpdateGenericReaction,
    'resolveSupportOrder': NothingToUpdateGenericReaction,
    'ravenChooseTrackBidsOrCollectTaxes': NothingToUpdateGenericReaction,
    'retreatUnitsAfterBattle': NothingToUpdateGenericReaction,
    'autoRetreatAfterBattle': NothingToUpdateGenericReaction,
    'throneChooseSupplyOrMuster': NothingToUpdateGenericReaction,
    'getEventCards': NothingToUpdateGenericReaction,
    'setEventCards': NothingToUpdateGenericReaction,
    'steelBladeChooseDisableMarchOrDefend': NothingToUpdateGenericReaction,

    'resolveCardLion5': NothingToUpdateGenericReaction,
    'chooseHouseCardAfterLion5': NothingToUpdateGenericReaction,
    'resolveCardRose2': NothingToUpdateGenericReaction,
    'killUnitsAfterBattle': NothingToUpdateGenericReaction,

    'wildlingsChooseKill2UnitsOr2PositionsOnTrack': NothingToUpdateGenericReaction,
    'wildlingsChooseTrackToBeFirstAt': NothingToUpdateGenericReaction,
    'wildlingsChooseTrackToBeLastAt': NothingToUpdateGenericReaction,
    'wildlingsDiscardHouseCard': NothingToUpdateGenericReaction,
    'wildlingsDowngradeKnights': NothingToUpdateGenericReaction,
    'wildlingsReturnHouseCard': NothingToUpdateGenericReaction,
    'wildlingsUpgradeKnights': NothingToUpdateGenericReaction,
    'disbandUnitsAfterCombat': NothingToUpdateGenericReaction,
    'disbandUnitDueToSupplies': NothingToUpdateGenericReaction,
    'calculateGameWinner': NothingToUpdateGenericReaction, # remove the game from the game data
}



class ActionReact:
    game_data: Optional[GamesDataService] = None
    redis: Optional[RedisConnector] = None

    @staticmethod
    @inject
    def init(game_data: GamesDataService = Provide[App.game_service],
             redis: RedisConnector = Provide[App.redis_service],
             events: EventSourcesService = Provide[App.events]):
        ActionReact.game_data = game_data
        ActionReact.redis = redis
        events.react_to_game_action.subscribe(on_next=ActionReact.react)

    @staticmethod
    def react(message: MessageGameAction[Action]):
        if 'reply' in message:
            game_id = message['gameId']
            for reply in message['reply']:  # type: Reply[Action]
                action = reply['player_action']
                if action['actionType'] == 'openOrders' and 'orders' in reply['player_action']:
                    OpenOrdersReaction(ActionReact.game_data.get_game(game_id).state, reply).update_game_state()
                elif action['actionType'] == 'setWildlingsCard':
                    SetWildlingsCardReaction(ActionReact.game_data.get_game(game_id).state, reply).update_game_state(game_data=ActionReact.game_data.get_game(game_id))
                elif action['actionType'] in switch_obj:
                    switch_obj[action['actionType']](ActionReact.game_data.get_game(game_id).state, reply).update_game_state()
                else:
                    raise Exception("{} action is not implemented".format(action['actionType']))

                if reply['player_action']['actionType'] not in [
                    "addOrder",
                    "openOrders",
                    "calculateCombatOutcome",
                    "autoKillUnitsAfterBattle",
                    "autoRetreatAfterBattle",
                    "killUnitsAfterBattle",
                    "retreatUnitsAfterBattle",
                    "ravenChooseTrackBidsOrCollectTaxes",
                    "wildlingsBids",
                    "trackBids"
                    ]:
                    new_game_state = GameState.from_json(reply['gameState'], ActionReact.game_data.game_rules)
                    local_game_handle = ActionReact.game_data.get_game(game_id)
                    if reply['player_action']['actionType'] == 'cleanUpAfterCombat':
                        local_game_handle.state.compare(new_game_state, ['AvailableOrders'])
                    elif reply['player_action']['actionType'] == 'setWildlingsCard':
                        local_game_handle.state.compare(new_game_state, ['Supplies'])
                    else:
                        local_game_handle.state.compare(new_game_state)

                react_to_phase(game_id, reply['current_phase'])