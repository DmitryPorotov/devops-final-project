from typing import Optional

from DTO.actions.planning import ActionAddOrder, ActionOpenOrders
from DTO.messages.messages import MessageGameAction
from DTO.phases.all_phases import SubPhase
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.order import Order
import uuid

from server_module.game_state.order_type import OrderType
from server_module.game_state.track_type import TrackType
from server_module.reactions.game_phase_reactions.base_phase_reaction import BasePhaseReaction


class AddOrderReaction(BasePhaseReaction):
    def __init__(self, game_id: str, house_type: HouseType, game_state: GameState, game_rules: GameRules, phase: SubPhase):
        super().__init__(game_id, house_type, game_state, game_rules, phase)
        self._total_stars = self._game_rules.get_total_num_stars_by_court_position(
            self._game_state.tracks[TrackType('court')].index(self._house_type)
        )

    # todo: implement this thought reactivex somehow
    __calls_by_round: dict[str, dict[HouseType: int]] = {}

    @staticmethod
    def delete_game(game_id: str):
        try:
            del AddOrderReaction.__calls_by_round[game_id]
        except KeyError:
            pass

    def get_actions(self) -> list[MessageGameAction[ActionAddOrder]]:
        my_armies = self._game_state.armies.get_armies_by_house_type(self._house_type)
        my_placed_orders = self._game_state.placed_orders[self._house_type] if (self._house_type
                                                                                in self._game_state.placed_orders) else {}
        if self._game_id not in AddOrderReaction.__calls_by_round:
            AddOrderReaction.__calls_by_round[self._game_id] = {}

        if self._house_type not in AddOrderReaction.__calls_by_round[self._game_id]:
            AddOrderReaction.__calls_by_round[self._game_id][self._house_type] = self._game_state.round_counter
        elif self._house_type in AddOrderReaction.__calls_by_round[self._game_id] and AddOrderReaction.__calls_by_round[self._game_id][self._house_type] == self._game_state.round_counter:
            return []
        AddOrderReaction.__calls_by_round[self._game_id][self._house_type] = self._game_state.round_counter

        return self._get_random_orders(my_armies, my_placed_orders)

    def _get_random_orders(self,
                           my_armies: dict[str, list[MilitaryUnit]],
                           my_placed_orders: dict[int, Order]) -> list[MessageGameAction[ActionAddOrder]]:
        armies_no_orders = {}
        for x in my_armies:
            if x not in my_placed_orders:
                armies_no_orders[x] = my_armies[x]

        avail_orders = self._game_state.available_orders[self._house_type]

        flat_avail_orders = []

        ## non-random
        flat_avail_orders.extend(avail_orders[OrderType.MARCH])
        flat_avail_orders.extend(avail_orders[OrderType.SUPPORT])
        flat_avail_orders.extend(avail_orders[OrderType.CONSOLIDATE_POWER])
        flat_avail_orders.extend(avail_orders[OrderType.DEFEND])
        flat_avail_orders.extend(avail_orders[OrderType.RAID])

        ## random
        # for t in avail_orders:
        #     flat_avail_orders.extend(avail_orders[t])
        # random.shuffle(flat_avail_orders)

        rnd_orders = {}
        idx_rnd_order = 0
        idx_army_no_order = 0
        stars_remaining = self._total_stars
        armies_no_orders_keys = list(armies_no_orders)
        while idx_rnd_order < len(flat_avail_orders) and idx_army_no_order < len(armies_no_orders_keys):
            if ((flat_avail_orders[idx_rnd_order].is_star and stars_remaining > 0)
                    or not flat_avail_orders[idx_rnd_order].is_star):
                rnd_orders[str(armies_no_orders_keys[idx_army_no_order])] = flat_avail_orders[idx_rnd_order]
                self._game_state.available_orders.use_order(self._house_type, flat_avail_orders[idx_rnd_order])
                if flat_avail_orders[idx_rnd_order].is_star:
                    stars_remaining -= 1
                idx_rnd_order += 1
                idx_army_no_order += 1
            elif stars_remaining == 0:
                idx_rnd_order += 1

        return self._to_json(rnd_orders)

    def __build_one_reply(self, tile_num: str, order: Order):
        json = super()._to_json()
        json['player_action'] = {
            'actionType': 'addOrder',
            'houseType': self._house_type,
            'tileNumber': int(tile_num),
            'order': order.to_json()
        }
        return json


    def _to_json(self, orders: dict[str, Order]):
        return (self.__build_one_reply(tn, o) for (tn, o) in orders.items())

    def finalizing_move_json(self, game_id) -> Optional[MessageGameAction[ActionOpenOrders]]:
        action: ActionOpenOrders = {
            'actionType': 'openOrders',
            'houseType': self._house_type,
        }
        message: MessageGameAction[ActionOpenOrders] = {
            'type': 'action',
            'userId': self._bot_id,
            'gameId': game_id,
            'messageId': str(uuid.uuid4()),
            'action': 'game_action',
            'player_action': action,
        }

        return message
