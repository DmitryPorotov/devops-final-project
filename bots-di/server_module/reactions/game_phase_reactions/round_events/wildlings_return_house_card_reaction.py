from typing import Optional

from DTO.actions.events import ActionWildlingsReturnHouseCard
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import choose_from_list


class WildlingsReturnHouseCardReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsReturnHouseCard]]:
        dhc = self._game_state.discarded_house_cards
        if self._house_type in dhc and dhc[self._house_type]:
            return [self._to_json(choose_from_list(dhc[self._house_type]))]
        else:
            return [self._to_json(None)]

    def _to_json(self, cc: Optional[int]) -> MessageGameAction[ActionWildlingsReturnHouseCard]:
        json = super()._to_json()
        action: ActionWildlingsReturnHouseCard = {
            'houseType': self._house_type,
            'actionType': 'wildlingsReturnHouseCard',
        }
        if cc:
            action['cardCode'] = cc

        json['player_action'] = action
        self.logger.info(json)
        return json