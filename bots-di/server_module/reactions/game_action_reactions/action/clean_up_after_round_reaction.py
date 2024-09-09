from DTO.actions.action import ActionCleanUpAfterRound
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.placed_orders import PlacedOrders
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class CleanUpAfterRoundReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionCleanUpAfterRound]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionCleanUpAfterRound = self._reply['player_action']
        self._game_state.placed_orders = PlacedOrders()
        self._game_state.round_counter = pa['round']
