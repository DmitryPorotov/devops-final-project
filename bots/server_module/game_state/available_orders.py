from server_module.game_rules.game_rules import GameRules
from server_module.game_state.order import Order, OrderType
from server_module.game_state.house_type import HouseType
from server_module.game_state.placed_orders import PlacedOrders


class AvailableOrders(dict[HouseType, dict[OrderType, list[Order]]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht, ao in kwargs.items():
                orders_by_type = {}
                for ot in ao:
                    orders = []
                    for i in range(len(ao[ot])):
                        orders.append(Order.from_json(ao[ot][i]))
                    orders_by_type[OrderType.from_str(ot)] = orders
                self[HouseType[ht.upper()]] = orders_by_type

    def build_from_placed_orders(self, game_rules: GameRules, placed_orders: PlacedOrders):
        for ht in HouseType:
            if ht is not HouseType.NEUTRAL:
                self[ht] = {}
                for ot, orders in game_rules.loaded_orders.items():
                    self[ht][ot] = []
                    for o in orders:
                        self[ht][ot].append(o)

        for ht in placed_orders:
            for tn, o in placed_orders[ht].items():
                idx = [i for i, j in enumerate(self[ht][o.order_type])
                       if j.modifier == o.modifier and j.is_star == o.is_star]
                if len(idx):
                    del self[ht][o.order_type][idx[0]]
