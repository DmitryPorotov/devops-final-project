from DTO.actions.events import ActionResolveTiesAfterBiddingOnTracks
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class ResolveTiesAfterBiddingOnTracksReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionResolveTiesAfterBiddingOnTracks]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionResolveTiesAfterBiddingOnTracks = self._reply['player_action']
        self._game_state.tracks[TrackType[pa['trackType'].upper()]] = [*(HouseType[t.upper()] for t in pa['resolution'])]