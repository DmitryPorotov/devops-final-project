from typing import Union

from DTO.actions.action import ActionResolveCardMoose2, ActionResolveCardLion1, ActionResolveCardMoose3, \
    ActionResolveCardWolf0
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

AfterCombatHouseCard = Union[ActionResolveCardMoose2, ActionResolveCardMoose3, ActionResolveCardLion1, ActionResolveCardWolf0]

class CleanUpAfterCombatReactionHouseCard(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[AfterCombatHouseCard]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: AfterCombatHouseCard = self._reply['player_action']
        self._game_state.combat = None
        self.logger.info(pa)