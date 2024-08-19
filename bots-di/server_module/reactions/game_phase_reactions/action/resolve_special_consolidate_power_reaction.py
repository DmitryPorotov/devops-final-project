import random
from typing import Optional

from DTO.actions.action import ActionResolveSpecialConsolidatePower
from DTO.messages.messages import MessageGameAction
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.game_state.order import Order
from server_module.game_state.order_type import OrderType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class ResolveSpecialConsolidatePowerReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules):
        super().__init__(game_id, house_type, game_state, game_rules)

    def get_actions(self) -> list[MessageGameAction]:
        dont_muster = not bool(random.randrange(0, 2))
        tn, order = self.__find_order()
        points_to_muster = self._game_rules.board[int(tn)].mustering_points
        if dont_muster or not points_to_muster:
            return [self._to_json(tn)]




    def __find_order(self) -> tuple[str, Order]:
        for tn, o in self._game_state.placed_orders[self._house_type].items():
            if o.order_type is OrderType.CONSOLIDATE_POWER and o.is_star:
                return tn, o


    def __find_possible_muster_units(self, points_to_muster: int, tile_num: str) -> MilitaryUnit:
        units_left_to_muster = self._game_state.armies.get_units_left_to_muster(self._game_rules, self._house_type)
        if points_to_muster == 1:
            # find something to upgrade
            pass
        else:
            # get unit for 2 points
            pass


    def _to_json(self, tile_num: int, unit: Optional[MilitaryUnit] = None) -> MessageGameAction[ActionResolveSpecialConsolidatePower]:
        json: MessageGameAction[ActionResolveSpecialConsolidatePower] = super()._to_json()
        action: ActionResolveSpecialConsolidatePower = {
            "actionType": 'resolveSpecialConsolidatePower',
            "tileNumber": tile_num,
            "houseType": self._house_type,
            'unit': unit
        }
        json['player_action'] = action
        return json