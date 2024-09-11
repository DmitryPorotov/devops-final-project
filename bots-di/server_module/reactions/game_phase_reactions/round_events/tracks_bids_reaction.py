from random import randrange

from dependency_injector.wiring import Provide, inject

from DTO.actions.events import ActionTrackBids
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseTracksBids
from containers_module import App
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.track_type import TrackType
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class TracksBidsReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionTrackBids]]:
        sp: SubPhaseTracksBids = self._phase
        self.__update_track_to_bid(TrackType[sp['trackType'].upper()])
        total_tokens = self._game_state.power_tokens[self._house_type] / 3
        tokens_to_bid = randrange(int(total_tokens) + 1)
        return [self._to_json(tokens_to_bid)]

    @inject
    def __update_track_to_bid(self, track: TrackType, games_service: GamesDataService = Provide[App.game_manager]):
        games_service.get_game(self._game_id).other['last_track'] = track

    def _to_json(self, num_tokens: int) -> MessageGameAction[ActionTrackBids]:
        json = super()._to_json()
        action: ActionTrackBids = {
            'houseType': self._house_type,
            'actionType': 'trackBids',
            'bid': num_tokens
        }
        json['player_action'] = action
        self.logger.info(json)
        return json