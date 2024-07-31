from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.order import Order
import random


class AddOrderReaction:
    def __init__(
            self,
            house_type: HouseType,
            game_state: GameState,
            total_stars: int
    ):
        self.game_state = game_state
        self.house_type = house_type
        self.total_stars = total_stars

    def get_moves(self) -> dict[int, Order]:
        my_armies = self.game_state.armies.get_armies_by_house_type(self.house_type)
        my_placed_orders = self.game_state.placed_orders[self.house_type] if (self.house_type
                                                                              in self.game_state.placed_orders) else {}
        return self._get_random_orders(my_armies, my_placed_orders)

    def _get_random_orders(self,
                           my_armies: dict[int, list[MilitaryUnit]],
                           my_placed_orders: dict[int, Order]) -> dict[int, Order]:
        armies_no_orders = {}
        for x in my_armies:
            if x not in my_placed_orders:
                armies_no_orders[x] = my_armies[x]

        avail_orders = self.game_state.available_orders[self.house_type]

        flat_avail_orders = []
        for t in avail_orders:
            flat_avail_orders.extend(avail_orders[t])
        random.shuffle(flat_avail_orders)
        rnd_orders = {}
        idx_rnd_order = 0
        idx_army_no_order = 0
        stars_remaining = self.total_stars
        armies_no_orders_keys = list(armies_no_orders)
        while idx_rnd_order < len(flat_avail_orders) and idx_army_no_order < len(armies_no_orders_keys):
            if ((flat_avail_orders[idx_rnd_order].is_star and stars_remaining > 0)
                    or not flat_avail_orders[idx_rnd_order].is_star):
                rnd_orders[armies_no_orders_keys[idx_army_no_order]] = flat_avail_orders[idx_rnd_order]
                if flat_avail_orders[idx_rnd_order].is_star:
                    stars_remaining -= 1
                idx_rnd_order += 1
                idx_army_no_order += 1
            elif stars_remaining == 0:
                idx_rnd_order += 1

        return rnd_orders

    def to_json(self, user_id: int, game_id: str, orders: dict[int, Order]):
        return ({
            'userId': user_id,
            'gameId': game_id,
            'action': 'game_action',
            'player_action': {
                'actionType': 'addOrder',
                'houseType': self.house_type,
                'tileNumber': int(tn),
                'order': o.to_json()
            }
        } for (tn, o) in orders.items())
