from game_state.game_state import GameState
from game_state.house_type import HouseType
from add_order_reaction import AddOrderReaction


def get_phase_reaction_inst(phase, house_type: HouseType, game_state: GameState):
    if phase['subPhase'] == 'addOrder':
        return AddOrderReaction(house_type, game_state)

