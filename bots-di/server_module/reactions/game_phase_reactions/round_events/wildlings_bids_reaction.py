from utils_ import randrange

from DTO.actions.events import ActionWildlingsBids
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class WildlingsBidsReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsBids]]:
        total_tokens = self._game_state.power_tokens[self._house_type]
        tokens_to_bid = randrange(total_tokens + 1)
        return [self._to_json(tokens_to_bid)]

    def _to_json(self, num_tokens: int) -> MessageGameAction[ActionWildlingsBids]:
        json = super()._to_json()
        action: ActionWildlingsBids = {
            'houseType': self._house_type,
            'actionType': 'wildlingsBids',
            'bid': num_tokens
        }
        json['player_action'] = action
        self.logger.info(json)
        return json