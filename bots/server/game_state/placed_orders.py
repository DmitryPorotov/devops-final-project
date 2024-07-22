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

    def place_order(self, house: HouseType, tile_num: int, order: Order, pos_on_cour_track: int):
        if house in self:
            if tile_num in self[house]:
                raise Exception("There is an order on this tile {:d} already".format(tile_num))
            else:
                self[house][tile_num] = order
        else:
            self[house] = {tile_num: order}
