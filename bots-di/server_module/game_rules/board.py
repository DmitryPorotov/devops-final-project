from server_module.game_rules.board_tile import BoardTile


class Board(list[BoardTile]):
    def __init__(self, *args: BoardTile):
        super().__init__()
        self.__castle_tiles: dict[str, BoardTile] = {}
        self.__castle_tiles_no_water: dict[str, BoardTile] = {}
        for tn, bt in enumerate(args):
            bt = BoardTile.from_json(bt)
            self.append(bt)
            if bt.mustering_points > 0:
                self.__castle_tiles[str(tn)] = bt
                if tn == 39 or tn == 28:  # note: I don't want to run checks on all neighbours, I know these are the only 2 castles
                    self.__castle_tiles_no_water[str(tn)] = bt

    def get_castle_tiles(self) -> dict[str, BoardTile]:
        return self.__castle_tiles

    def get_castle_tiles_no_water(self) -> dict[str, BoardTile]:
        return self.__castle_tiles_no_water