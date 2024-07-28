from enum import StrEnum, auto


class OrderType(StrEnum):
    MARCH = auto()
    DEFEND = auto()
    SUPPORT = auto()
    RAID = auto()
    CONSOLIDATE_POWER = 'consolidatePower'

    @classmethod
    def from_str(cls, string: str):
        if string == 'consolidatePower':
            return cls.CONSOLIDATE_POWER
        else:
            return cls[string.upper()]
