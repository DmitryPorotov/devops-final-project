from DTO.actions.planning import ActionRavenChoosePutWildlingsCardOnTopOrBottom
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
import random


class RavenChoosePutWildlingsCardOnTopOrBottomReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionRavenChoosePutWildlingsCardOnTopOrBottom]]:
        return [self._to_json()]

    def _to_json(self) -> MessageGameAction[ActionRavenChoosePutWildlingsCardOnTopOrBottom]:
        is_top = bool(random.randrange(0, 2))
        json = super()._to_json()
        json['player_action'] = {
            'actionType': 'ravenChoosePutWildlingsCardOnTopOrBottom',
            'houseType': self._house_type,
            'isPutOnTop': is_top
        }
        return json