from server_module.game_state.house_type import HouseType
from server_module.game_state.order import Order
from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


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

    def place_order(self, house: HouseType, tile_num: int | str, order: Order, pos_on_cour_track: int):
        tile_num = str(tile_num)
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

    def compare(self, other: "PlacedOrders") -> bool:
        for ht, orders in other.items():
            if ht not in self and len(orders):
                raise StateDiscrepancyException("House {} of other is not in local PlacedOrders".format(ht))
            for tn, order in orders.items():
                if tn not in self[ht]:
                    raise StateDiscrepancyException("House {} tile number {} of other is not in local PlacedOrders".format(ht, tn))
                if not order.__eq__(self[ht][tn]):
                    raise StateDiscrepancyException("House {} tile number {} order of other {} is not equal to local {} order in PlacedOrders".format(ht, tn, order, self[ht][tn]))
        for s_ht, s_orders in self.items():
            if s_ht not in other and len(s_orders):
                raise StateDiscrepancyException("House {} of local is not in other PlacedOrders".format(s_ht))
            for tn, order in s_orders.items():
                if tn not in other[s_ht]:
                    raise StateDiscrepancyException("House {} tile number {} of local is not in other PlacedOrders".format(s_ht, tn))
        return True