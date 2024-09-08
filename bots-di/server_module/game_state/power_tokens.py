from server_module.game_state.house_type import HouseType


class PowerTokens(dict[HouseType, int]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht, tokens in kwargs.items():  # type: str, int
                self[HouseType[ht.upper()]] = tokens
