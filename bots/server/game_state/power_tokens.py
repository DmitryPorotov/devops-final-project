from server.game_state.house_type import HouseType


class PowerTokens(dict[HouseType, int]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht in kwargs:
                self[HouseType[ht.upper()]] = kwargs[ht]
