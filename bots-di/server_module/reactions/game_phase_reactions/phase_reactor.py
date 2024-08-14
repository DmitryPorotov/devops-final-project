from DTO.phases.all_phases import SubPhase
from server_module.reactions.game_phase_reactions.phase_multi_house_reaction import MultiHouseReact
from server_module.reactions.game_phase_reactions.planning.add_order_reaction import AddOrderReaction
from server_module.reactions.game_phase_reactions.planning.raven_choose_change_order_or_look_at_wildling_card import \
    RavenChooseChangeOrderOrLookAtWildlingCardReaction

switcher_obj = {
    'addOrder':AddOrderReaction,
    'ravenChooseChangeOrderOrLookAtWildlingCard': RavenChooseChangeOrderOrLookAtWildlingCardReaction,
}

def react_to_phase(game_id: str, phase: SubPhase):
    MultiHouseReact.react(switcher_obj[phase['subPhase']], game_id, phase)

