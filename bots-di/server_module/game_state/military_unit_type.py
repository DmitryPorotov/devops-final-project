from enum import StrEnum, auto


class MilitaryUnitType(StrEnum):
    FOOTMEN = auto()
    KNIGHTS = auto()
    SHIPS = auto()
    SIEGE_ENGINES = 'siegeEngines'
    GARRISON = auto()
    POWER_TOKEN = 'powerToken'

    @classmethod
    def from_str(cls, string: str):
        if string == 'siegeEngines':
            return cls.SIEGE_ENGINES
        elif string == 'powerToken':
            return cls.POWER_TOKEN
        else:
            return cls[string.upper()]


    def get_mustering_points(self) -> int:
        if self is MilitaryUnitType.SHIPS or self is MilitaryUnitType.FOOTMEN:
            return 1
        elif self is MilitaryUnitType.KNIGHTS or self is MilitaryUnitType.SIEGE_ENGINES:
            return 2
        else:
            return 0

    # note: do I even need this for bots?
    def get_strength(self, is_attacking_castle: bool = False) -> int:
        if self is MilitaryUnitType.SHIPS or self is MilitaryUnitType.FOOTMEN:
            return 1
        elif self is MilitaryUnitType.KNIGHTS:
            return 2
        elif self is MilitaryUnitType.SIEGE_ENGINES and is_attacking_castle:
            return 4
        elif self is MilitaryUnitType.GARRISON:
            raise RuntimeError("See garrison defence points instead.")
        else:
            return 0