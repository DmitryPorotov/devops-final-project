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
