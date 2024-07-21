from server.game_state.order import Order, OrderType
from server.game_state.house_type import HouseType


class AvailableOrders(dict[HouseType, dict[OrderType, list[Order]]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht in kwargs:
                orders_by_type = {}
                for ot in kwargs[ht]:
                    orders = []
                    for i in range(len(kwargs[ht][ot])):
                        orders.append(Order.from_json(kwargs[ht][ot][i]))
                    orders_by_type[OrderType.from_str(ot)] = orders
                self[HouseType[ht.upper()]] = orders_by_type
