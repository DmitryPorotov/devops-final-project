from server_module.game_state.house_type import HouseType

type CardCode = int


class DiscardedHouseCards(dict[HouseType, list[CardCode]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht in kwargs:
                self[HouseType[ht.upper()]] = kwargs[ht]