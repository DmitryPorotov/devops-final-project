from server.game_state.house_type import HouseType
from server.game_state.military_unit_type import MilitaryUnitType


class MilitaryUnit:
    def __init__(self,
                 house: HouseType,
                 unit_type: MilitaryUnitType,
                 is_defeated: bool = False,
                 garrison_defence_points: int = 0
                 ):
        self.house = house
        self.unit_type = unit_type
        self.is_defeated = is_defeated
        self.garrison_defence_points = garrison_defence_points

    @classmethod
    def from_json(cls, json):
        return cls(
            HouseType[json['house'].upper()],
            MilitaryUnitType.from_str(json['type']),
            json['isDefeated'] if 'isDefeated' in json else False,
            json['defPoints'] if 'defPoints' in json else 0
        )