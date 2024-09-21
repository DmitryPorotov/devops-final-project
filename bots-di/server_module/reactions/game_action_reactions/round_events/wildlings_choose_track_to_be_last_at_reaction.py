from DTO.actions.events import ActionWildlingsChooseTrackToBeLastAt
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsChooseTrackToBeLastAtReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsChooseTrackToBeLastAt]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsChooseTrackToBeLastAt = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        track = TrackType[pa['track'].upper()]
        idx = self._game_state.tracks[track].index(house)
        self._game_state.tracks[track].pop(idx)
        self._game_state.tracks[track].append(house)
        self.logger.info(pa)
