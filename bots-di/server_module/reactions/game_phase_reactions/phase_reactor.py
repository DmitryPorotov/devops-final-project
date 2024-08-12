from DTO.phases.all_phases import SubPhase
from server_module.reactions.game_phase_reactions.multi_house_reaction import MultiHouseReact
from server_module.reactions.game_phase_reactions.planning.add_order_reaction import AddOrderReaction


def react_to_phase(game_id: str, phase: SubPhase):
    if phase['subPhase'] == 'addOrder':
        MultiHouseReact().react(AddOrderReaction, game_id, phase)
