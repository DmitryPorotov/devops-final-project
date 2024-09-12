from server_module.game_state.house_type import HouseType
from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


class Bids(dict[HouseType, int]):
    def __init__(self, **kwargs):
        super().__init__()
        for k, v in kwargs.items():
            self[HouseType[k.upper()]] = v

    def compare(self, other: "Bids") -> bool:
        for ht, n in self.items():
            if n != other[ht]:
                raise StateDiscrepancyException("Bids of {} in local {} are not equal to other {}".format(ht, n, other[ht]))
        return True