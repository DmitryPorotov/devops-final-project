from DTO.actions.action import ActionResolveConsolidatePowerOrder
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.power_tokens import PowerTokens
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveConsolidatePowerOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveConsolidatePowerOrder]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveConsolidatePowerOrder = self._reply['player_action']
        self._game_state.power_tokens = self._game_state['powerTokens'] = PowerTokens(**pa['powerTokens'])
        self.logger.info(pa)