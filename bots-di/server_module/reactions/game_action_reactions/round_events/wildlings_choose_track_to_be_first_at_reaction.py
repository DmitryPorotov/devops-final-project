from DTO.actions.events import ActionWildlingsChooseTrackToBeFirstAt
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsChooseTrackToBeFirstAtReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsChooseTrackToBeFirstAt]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsChooseTrackToBeFirstAt = self._reply['player_action']
        track_type = TrackType[pa['track'].upper()]
        track = self._game_state.tracks[track_type]
        house = HouseType[pa['houseType'].upper()]
        idx = track.index(house)
        track.pop(idx)
        new_track = [house]
        new_track.extend(track)
        self._game_state.tracks[track_type] = new_track
        self.logger.info(pa)
