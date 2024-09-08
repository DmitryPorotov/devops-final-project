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

    @classmethod
    def build_from_placed_orders(cls, game_rules: GameRules, placed_orders: PlacedOrders):
        inst = cls()
        for ht in HouseType:
            if ht is not HouseType.NEUTRAL:
                inst[ht] = {}
                for ot, orders in game_rules.loaded_orders.items():
                    inst[ht][ot] = []
                    for o in orders:
                        inst[ht][ot].append(o)

        for ht in placed_orders:
            for tn, o in placed_orders[ht].items():
                idx = [i for i, j in enumerate(inst[ht][o.order_type])
                       if j.modifier == o.modifier and j.is_star == o.is_star]
                if len(idx):
                    del inst[ht][o.order_type][idx[0]]
        return inst

    def use_order(self, house: HouseType, order: Order):
        idx = -1
        for i, o in enumerate(self[house][order.order_type]):
            if o.is_star == order.is_star and o.modifier == order.modifier:
                idx = i
                break
        if idx >= 0:
            self[house][order.order_type].pop(idx)
        else:
            raise RuntimeError('Order "{}" modifier "{}" does not is not available'.format(order.order_type, order.modifier))