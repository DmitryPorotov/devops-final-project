from enum import StrEnum, auto


class BoardTileType(StrEnum):
    SEA = auto()
    LAND = auto()
    PORT = auto()