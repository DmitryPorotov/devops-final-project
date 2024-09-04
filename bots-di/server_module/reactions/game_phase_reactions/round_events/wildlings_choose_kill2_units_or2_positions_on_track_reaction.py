from random import randrange

from DTO.actions.events import ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class WildlingsChooseKill2UnitsOr2PositionsOnTrackReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack]]:
        highest_track_pos: int = 10
        tracks = []
        for track_type, track in self._game_state.tracks.items():
            if (idx := track.index(self._house_type)) == highest_track_pos:
                tracks.append(track_type)
            elif idx < highest_track_pos:
                highest_track_pos = idx
                tracks = [track_type]

        # only do tracks for now
        idx2 = randrange(len(tracks))
        return [self._to_json(track=tracks[idx2])]

    def _to_json(self, track: str=None, units:dict[str, MilitaryUnit]=None) -> MessageGameAction[ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack]:
        json = super()._to_json()
        action: ActionWildlingsChooseKill2UnitsOr2PositionsOnTrack = {
            'houseType': self._house_type,
            'actionType': 'wildlingsChooseKill2UnitsOr2PositionsOnTrack',
        }
        if track:
            action['track'] = track
        if units:
            track['units'] = units
        json['player_action'] = action
        return json