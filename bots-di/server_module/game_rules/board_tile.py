from typing import Optional

from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_state.house_type import HouseType


class BoardTile:
    def __init__(
            self,
            tile_type: BoardTileType,
            name: str,
            neighbour_tiles: list[int],
            mustering_points: int = 0,
            supply_points: int = 0,
            power_points: int = 0,
            home_of: Optional[HouseType] = None
    ):
        self.tile_type = tile_type
        self.name = name
        self.neighbour_tiles = neighbour_tiles
        self.mustering_points = mustering_points
        self.supply_points = supply_points
        self.power_points = power_points
        self.home_of = home_of

    @classmethod
    def from_json(cls, json):
        return cls(
            BoardTileType[json['tileType'].upper()],
            json['name'],
            json['neighbourTiles'],
            json['musteringPoints'] if 'musteringPoints' in json else 0,
            json['supplyPoints'] if 'supplyPoints' in json else 0,
            json['powerPoints'] if 'powerPoints' in json else 0,
            HouseType[json['homeOf'].upper()] if ('homeOf' in json and json['homeOf'] is not None) else None,
        )
