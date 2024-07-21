from game_state.game_state import GameState
from game_state.house_type import HouseType
from game_state.military_unit import MilitaryUnit
from game_state.order import Order
import random


class AddOrderReaction:
    def __init__(
            self,
            house_type: HouseType,
            game_state: GameState
    ):
        self.game_state = game_state
        self.house_type = house_type

    def react(self):
        my_armies = self.game_state.armies.get_armies_by_house_type(self.house_type)
        my_placed_orders = self.game_state.placed_orders[self.house_type]
        rnd_orders = self._get_random_orders(my_armies, my_placed_orders)

    def _get_random_orders(self,
                           my_armies: dict[int, list[MilitaryUnit]],
                           my_placed_orders: dict[int, Order]):
        armies_no_orders = {}
        for x in my_armies:
            if x not in my_placed_orders:
                armies_no_orders[x] = my_armies[x]

        avail_orders = self.game_state.available_orders[self.house_type]

        flat_avail_orders = []
        for t in avail_orders:
            flat_avail_orders.extend(avail_orders[t])
        random.shuffle(flat_avail_orders)
        rnd_orders = []
        idx = 0
        for a in armies_no_orders:
            rnd_orders[a] = flat_avail_orders[idx]
            idx += 1

        return rnd_orders
