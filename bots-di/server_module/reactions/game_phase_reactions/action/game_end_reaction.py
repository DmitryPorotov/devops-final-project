from dependency_injector.wiring import Provide, inject

from DTO.phases.all_phases import SubPhase
from containers_module import App
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from server_module.reactions.game_phase_reactions.no_reply_needed_exception import NoReplyNeedException


class GameEndReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self):

        raise NoReplyNeedException()

    @inject
    def __clean_up_game(self, games_data_service: GamesDataService = Provide[App.game_manager]):
        # games_data_service.delete_game(self._game_id)
        pass