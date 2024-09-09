from DTO.messages.messages import Message
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from server_module.reactions.game_phase_reactions.no_reply_needed_exception import NoReplyNeedException


class CleanUpAfterRoundReaction(BasePhaseReaction):
    _requested_game_state_by_round: dict[int: bool] = {}

    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[Message]:
        if self._game_state.round_counter not in self._requested_game_state_by_round:
            self._requested_game_state_by_round[self._game_state.round_counter] = True
            return [self._to_json()]
        else:
            raise NoReplyNeedException()

    def _to_json(self) -> Message:
        json: Message = super()._to_json()
        json['action'] = 'get_partial_game_state'
        json['parts'] = ['tracks', 'roundCounter', 'powerTokens']
        return json
