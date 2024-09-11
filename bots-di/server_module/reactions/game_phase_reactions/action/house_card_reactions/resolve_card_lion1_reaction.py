import random

from DTO.actions.action import ActionResolveCardLion1
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveCardLion1Reaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)

    def _to_json(self, **kwargs) -> MessageGameAction[ActionResolveCardLion1]:
        json: MessageGameAction[ActionResolveCardLion1] = super()._to_json()
        action: ActionResolveCardLion1 = {
            "houseType": self._house_type,
            "actionType": 'resolveCardLion1',
            'tileNumber': self.__find_order_to_remove()
        }
        json['player_action'] = action
        self.logger.info(json)
        return json

    def __find_order_to_remove(self) -> int:
        combat = self._game_state.combat
        orders = dict(self._game_state.placed_orders[self.__get_opponent_house()])
        del orders[str(combat.defender_tile_num)]  # this order is removed anyway because lion won
        orders_tuples = [*((tn, o) for tn, o in orders.items())]
        if (l := len(orders_tuples)) > 1:
            idx = random.randrange(l)
            return int(orders_tuples[idx][0])
        elif l == 1:
            return int(orders_tuples[0][0])
        else:
            return -1

    def __get_opponent_house(self) -> HouseType:
        combat = self._game_state.combat
        if combat.attacker_house is self._house_type:
            return combat.defender_house
        else:
            return combat.defender_house