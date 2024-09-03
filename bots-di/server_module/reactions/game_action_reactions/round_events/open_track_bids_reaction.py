from DTO.actions.events import ActionOpenTrackBids
from DTO.messages.reply import Reply
from server_module.game_state.bids import Bids
from server_module.game_state.game_state import GameState
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction


class NonepenTrackBidsReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionOpenTrackBids]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionOpenTrackBids = self._reply['player_action']
        self._game_state.bids = Bids(**pa['bids'])
