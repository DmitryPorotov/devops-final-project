from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order


class PlacedOrders(dict[HouseType, dict[str, Order]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht, os in kwargs.items():
                orders = {}
                for tile_num, o in os.items():
                    orders[tile_num] = Order.from_json(o)
                self[HouseType[ht.upper()]] = orders

    def place_order(self, house: HouseType, tile_num: int, order: Order, pos_on_cour_track: int):
        if house in self:
            if tile_num in self[house]:
                raise Exception("There is an order on this tile {:d} already".format(tile_num))
            else:
                self[house][tile_num] = order
        else:
            self[house] = {tile_num: order}

    def remove_order(self, tile_num: str | int, house_type: HouseType = None):
        tile_num = str(tile_num)
        if house_type and house_type in self and tile_num in self[house_type]:
            del self[house_type][tile_num]
            if len(self[house_type]) == 0:
                del self[house_type]
        elif not house_type:
            for ht, orders in self.items():
                for tn, _ in orders.items():
                    if tn == tile_num:
                        del self[ht][tn]
                        if len(self[ht]) == 0:
                            del self[ht]
                        return