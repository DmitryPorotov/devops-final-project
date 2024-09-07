from DTO.actions.events import ActionWildlingsDiscardHouseCard
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
from utils_ import choose_from_list


class WildlingsDiscardHouseCardReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def get_actions(self) -> list[MessageGameAction[ActionWildlingsDiscardHouseCard]]:
        my_discarded_cards = self._game_state.discarded_house_cards[self._house_type] if self._house_type in self._game_state.discarded_house_cards else []
        my_cards = [*(cc for cc in range(7) if cc not in my_discarded_cards)]
        return [self._to_json(choose_from_list(my_cards))]

    def _to_json(self, cc: int) -> MessageGameAction[ActionWildlingsDiscardHouseCard]:
        json = super()._to_json()
        action: ActionWildlingsDiscardHouseCard = {
            'houseType': self._house_type,
            'actionType': 'wildlingsDiscardHouseCard',
            'cardCode': cc
        }
        json['player_action'] = action
        return json