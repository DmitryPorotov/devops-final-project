from server_module.game_rules.game_rules import GameRules
from server_module.game_state.order import Order, OrderType
from server_module.game_state.house_type import HouseType
from server_module.game_state.placed_orders import PlacedOrders
from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


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
            raise RuntimeError('Order "{}" modifier "{}" is not available'.format(order.order_type, order.modifier))

    def return_order(self, house: HouseType, order: Order):
        self[house][order.order_type].append(order)

    def compare(self, other: "AvailableOrders") -> bool:
        return True
        # for ht, orders in self.items():
        #     if ht not in other:
        #         raise StateDiscrepancyException("AvailableOrders local house {} is not in other".format(ht))
        #     for ot, os in orders.items():
        #         if ot not in other[ht]:
        #             raise StateDiscrepancyException("AvailableOrders local house {} order type '{}' is not in other".format(ht, ot))
        #         key = lambda o: "{}-{}-{}".format(o.order_type, o.modifier, o.is_star)
        #         local = sorted(os, key=key)
        #         others = sorted(other[ht][ot], key=key)
        #         if len(local) != len(others):
        #             raise StateDiscrepancyException("AvailableOrders local house {} order type '{}' number of orders is not equal to other".format(ht, ot))
        #         for i in range(len(local)):
        #             if not local[i].__eq__(others[i]):
        #                 raise StateDiscrepancyException("AvailableOrders local house {} order type '{}' order at {} is not equal to other".format(ht, ot, i))
        # for ht, orders in other.items():
        #     if ht not in self:
        #         raise StateDiscrepancyException("AvailableOrders other house {} is not in local".format(ht))
        # return True