from DTO.actions.action import ActionCleanUpAfterCombat
from DTO.messages.reply import Reply
from server_module.game_state.armies import Armies
from server_module.game_state.discarded_house_cards import DiscardedHouseCards
from server_module.game_state.game_state import GameState
from server_module.game_state.placed_orders import PlacedOrders
from server_module.game_state.power_tokens import PowerTokens
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class CleanUpAfterCombatReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionCleanUpAfterCombat]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionCleanUpAfterCombat = self._reply['player_action']
        state_json = pa['state']
        self._game_state.combat = None
        self._game_state.armies = Armies(**state_json['armies'])
        self._game_state.placed_orders = PlacedOrders(**state_json['placedOrders'])
        self._game_state.discarded_house_cards = DiscardedHouseCards(**state_json['discardedHouseCards'])
        self._game_state.power_tokens = PowerTokens(**state_json['powerTokens'])
