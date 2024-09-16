from DTO.actions.all_actions import Action
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class NothingToUpdateGenericReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[Action]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: Action = self._reply['player_action']
        self.logger.info(pa)