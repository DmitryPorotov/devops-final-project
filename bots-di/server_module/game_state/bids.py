from server_module.game_state.house_type import HouseType


class Bids(dict[HouseType, int]):
    def __init__(self, **kwargs):
        super().__init__()
        for k, v in kwargs.items():
            self[HouseType[k.upper()]] = v