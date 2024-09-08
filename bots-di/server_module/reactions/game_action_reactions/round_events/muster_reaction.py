from DTO.actions.events import ActionMuster
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class MusterReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionMuster]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionMuster = self._reply['player_action']
        for tile, points in pa['usedPoints'].items():
            self._game_state.used_mustering_points[int(tile)] = points
