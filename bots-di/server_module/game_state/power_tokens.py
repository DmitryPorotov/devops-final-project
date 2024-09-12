from server_module.game_state.house_type import HouseType
from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


class PowerTokens(dict[HouseType, int]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht, tokens in kwargs.items():  # type: str, int
                self[HouseType[ht.upper()]] = tokens

    def compare(self, other: "PowerTokens") -> bool:
        for ht, n in self.items():
            if n != other[ht]:
                raise StateDiscrepancyException("PowerTokens of {} in local {} are not equal to other {}".format(ht, n, other[ht]))
        return True