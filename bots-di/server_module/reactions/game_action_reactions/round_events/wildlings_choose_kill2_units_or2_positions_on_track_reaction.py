from DTO.actions.events import ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack
from DTO.messages.reply import Reply
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_action_reactions.base_action_reaction import BaseActionReaction

class WildlingsChooseKill2UnitsOr2PositionsOnTrackReaction(BaseActionReaction):
    def __init__(self, game_state: GameState, reply: Reply[ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack]):
        super().__init__(game_state, reply)

    def update_game_state(self):
        pa: ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack = self._reply['player_action']
        house = HouseType[pa['houseType'].upper()]
        if 'track' in pa and pa['track']:
            track = TrackType[pa["track"].upper()]
            idx = self._game_state.tracks[track].index(house)
            self._game_state.tracks[track].pop(idx)
            self._game_state.tracks[track].insert(idx + 2, house)
        else:
            for tile_num, unit in pa['units']:
                unit_inst = MilitaryUnit.from_json(unit)
                idx = self._game_state.armies[tile_num].index(unit_inst)
                self._game_state.armies[tile_num].pop(idx)
        self.logger.info(pa)
