from DTO.actions.events import ActionCollectTaxes
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.power_tokens import PowerTokens
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class CollectTaxesReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionCollectTaxes]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionCollectTaxes = self._reply['player_action']
        self._game_state.power_tokens = PowerTokens(**pa['powerTokens'])
