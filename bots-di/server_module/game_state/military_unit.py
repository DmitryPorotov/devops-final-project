from server_module.game_state.house_type import HouseType
from server_module.game_state.military_unit_type import MilitaryUnitType


class MilitaryUnit(dict[str, HouseType | MilitaryUnitType | bool | int]):
    def __init__(self,
                 house: HouseType,
                 unit_type: MilitaryUnitType,
                 is_defeated: bool = False,
                 garrison_defence_points: int = 0
                 ):
        super().__init__({
            'house': house,
            'type': unit_type,
            'isDefeated': is_defeated,
            'defPoints': garrison_defence_points,
        })
        self.house = house
        self.unit_type = unit_type
        self.is_defeated = is_defeated
        self.garrison_defence_points = garrison_defence_points

    def __eq__(self, other: dict[str, HouseType | MilitaryUnitType | bool | int]):
        return str(self.unit_type) == other['type'] and str(self.house) == other['house'] and (self.is_defeated == other['isDefeated'] if 'isDefeated' in other else True)\
            and (self.garrison_defence_points if 'defPoints' in other else True)

    @classmethod
    def from_json(cls, json) -> "MilitaryUnit":
        return cls(
            HouseType[json['house'].upper()],
            MilitaryUnitType.from_str(json['type']),
            json['isDefeated'] if 'isDefeated' in json else False,
            json['defPoints'] if 'defPoints' in json else 0
        )