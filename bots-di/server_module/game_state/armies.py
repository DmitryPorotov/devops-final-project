from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.house_type import HouseType


class Armies(dict[str, list[MilitaryUnit]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for k in kwargs:
                val = []
                for mu in kwargs[k]:
                    val.append(MilitaryUnit.from_json(mu))
                self[k] = val

    def get_armies_by_house_type(self, house_type: HouseType) -> dict[int, list[MilitaryUnit]]:
        ret_val = {}
        for tile in self:
            if self[tile][0].house == house_type:
                ret_val[tile] = self[tile]
        return ret_val
