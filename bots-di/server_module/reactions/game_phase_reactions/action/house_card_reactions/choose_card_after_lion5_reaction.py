import random

from DTO.actions.action import ActionChooseHouseCardAfterLion5
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ChooseHouseCardAfterLion5Reaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def _to_json(self) -> MessageGameAction[ActionChooseHouseCardAfterLion5]:
        json: MessageGameAction[ActionChooseHouseCardAfterLion5] = super()._to_json()
        new_cc = self.__choose_new_card()
        action: ActionChooseHouseCardAfterLion5 = {
            "houseType": self._house_type,
            "actionType": 'chooseHouseCardAfterLion5',
            "cardCode": new_cc
        }
        json['player_action'] = action
        return json

    def __choose_new_card(self) -> int:
        discarded = list(self._game_state.discarded_house_cards[self._house_type] if self._house_type in self._game_state.discarded_house_cards else [])
        combat = self._game_state.combat
        prev_card = combat.attacker_card.code if combat.attacker_house == self._house_type else combat.defender_card.code
        discarded.append(prev_card)
        available = [*(x for x in range(7) if x not in discarded)]
        if len(available) > 1:
            idx = random.randrange(len(available))
            return available[idx]
        else:
            return available[0]
