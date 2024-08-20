from DTO.actions.action import ActionResolveMarchOrder
from DTO.messages.reply import Reply
from server_module.game_state.combat import Combat
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class ResolveMarchOrderReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveMarchOrder]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        action: ActionResolveMarchOrder = self._reply['player_action']
        if 'combat' in self._reply:
            self._game_state.combat = Combat.from_json(self._reply['combat'])
