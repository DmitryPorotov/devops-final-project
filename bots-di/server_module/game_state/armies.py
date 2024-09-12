from typing import Generator

from server_module.game_rules.game_rules import GameRules
from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


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

    def get_armies_by_house_type_generator(self, house_type: HouseType) -> Generator[tuple[str, list[MilitaryUnit]], None, None]:
        for tile in self:
            if self[tile] and self[tile][0].house == house_type:
                yield tile, self[tile]

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

    @staticmethod
    def __mil_unit_sort_key(item: MilitaryUnit):
        return "{}-{}".format(item.unit_type, item.is_defeated)

    def compare(self, other: "Armies") -> bool:
        for tn, army in other.items():
            if tn not in self:
                raise StateDiscrepancyException("Tile number {} is not in local Armies".format(tn))
            local_army = sorted(self[tn], key=self.__mil_unit_sort_key)
            other_army = sorted(army, key=self.__mil_unit_sort_key)
            if len(local_army) != len(other_army):
                raise StateDiscrepancyException("Tile number {} armies has different number of units; local {}, other {}".format(tn, len(local_army), len(other_army)))
            for i in range(len(local_army)):
                if not local_army[i].__eq__(other_army[i]):
                    raise StateDiscrepancyException("Tile number {} armies have different units; local {}, other {}".format(tn, local_army[i], other_army[i]))
        for s_tn, s_army in self.items():
            if s_tn not in other:
                raise StateDiscrepancyException("Tile number {} is not in other Armies".format(s_tn))
        return True