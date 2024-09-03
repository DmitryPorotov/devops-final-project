from server_module.game_rules.game_rules import GameRules
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit_type import MilitaryUnitType


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

    def get_armies_by_house_type(self, house_type: HouseType) -> dict[str, list[MilitaryUnit]]:
        ret_val = {}
        for tile in self:
            if self[tile] and self[tile][0].house == house_type:
                ret_val[tile] = self[tile]
        return ret_val

    def get_units_left_to_muster(self, game_rules: GameRules, house_type: HouseType) -> dict[MilitaryUnitType, int]:
        max_armies = dict(game_rules.max_armies)
        for _, units in self.items():
            if len(units) and units[0].house != house_type:
                continue
            else:
                for u in units:
                    if u.unit_type.get_mustering_points():
                        max_armies[u.unit_type] -= 1

        return max_armies
