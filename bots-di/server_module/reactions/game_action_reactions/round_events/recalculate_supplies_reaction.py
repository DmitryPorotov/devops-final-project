from DTO.actions.events import ActionRecalculateSupplies
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.supplies import Supplies
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class RecalculateSuppliesReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionRecalculateSupplies]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionRecalculateSupplies = self._reply['player_action']
        self._game_state.supplies = self._game_state['supplies'] = Supplies(**pa['supplies'])
