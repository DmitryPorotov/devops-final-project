from DTO.actions.action import ActionUseValyrianSteelBlade
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class UseValyrianSteelBladeReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionUseValyrianSteelBlade]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionUseValyrianSteelBlade = self._reply['player_action']
        if pa['choice'] != 'nothing':
            self._game_state.dominance_tokens_usage['valyrianSword'] = True
        self.logger.info(pa)