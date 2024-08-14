from typing import Optional

from DTO.actions.all_actions import Action
from DTO.messages.messages import MessageGameAction
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction
import random


class RavenChooseChangeOrderOrLookAtWildlingCardReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules):
        super().__init__(game_id, house_type, game_state, game_rules)
        self.__raven_choices = ['nothing', 'changeOrder', 'lookAtWildlingsCard']

    def get_actions(self) -> list[MessageGameAction]:
        return self._to_json()

    def _to_json(self):
        json = super()._to_json()[0]
        # random.shuffle(self.__raven_choices)
        json['player_action'] = {
            'actionType': 'ravenChooseChangeOrderOrLookAtWildlingCard',
            'houseType': self._house_type,
            'ravenChoice': self.__raven_choices[2]
        }
        return [json]

    def finalizing_move_json(self, game_id) -> Optional[MessageGameAction[Action]]:
        return None