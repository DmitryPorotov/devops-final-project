import logging

from DTO.actions.all_actions import Action
from server_module.game_state.game_state import GameState
from DTO.messages.reply import Reply


class BaseActionReaction:
    def __init__(self, game_state: GameState, reply: Reply[Action]):
        self._game_state = game_state
        self._reply = reply
        self.logger = logging.getLogger(
            f"{__name__}.{self.__class__.__name__}",
        )

    def update_game_state(self, *args):
        pass

