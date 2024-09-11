from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import SubPhaseWildlingsCard
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from server_module.reactions.game_phase_reactions.no_reply_needed_exception import NoReplyNeedException


class WildlingsCardReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self):
        sp: SubPhaseWildlingsCard = self._phase
        self._game_state.board_cards.add_wildling_card(sp['cardCode'])
        raise NoReplyNeedException()
