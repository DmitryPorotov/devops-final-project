from DTO.phases.all_phases import SubPhase
from DTO.phases.phases import  SubPhaseSetEventCards
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from server_module.reactions.game_phase_reactions.no_reply_needed_exception import NoReplyNeedException


class SetEventCardsReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self):
        sp: SubPhaseSetEventCards = self._phase
        self._game_state.board_cards.add_round_events1_card(sp['card1'])
        self._game_state.board_cards.add_round_events2_card(sp['card2'])
        self._game_state.board_cards.add_round_events3_card(sp['card3'])
        raise NoReplyNeedException()
