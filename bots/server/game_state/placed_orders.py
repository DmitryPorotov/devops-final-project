from server.game_state.house_type import HouseType
from server.game_state.order import Order


class PlacedOrders(dict[HouseType, dict[int, Order]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht in kwargs:
                orders = {}
                for tile_num in kwargs[ht]:
                    orders[tile_num] = Order.from_json(kwargs[ht][tile_num])
                self[HouseType[ht]] = orders
