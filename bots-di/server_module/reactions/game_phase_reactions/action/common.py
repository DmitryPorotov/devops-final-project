from server_module.game_rules.board_tile import BoardTile
from server_module.game_rules.board_tile_type import BoardTileType
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from utils_ import print_file_lineno_error


def find_reachable_targets(game_rules: GameRules, game_state: GameState, house_type: HouseType, source_tile: BoardTile, candidates: list[int] = [], visited_seas: list[int] = []) -> list[int]:
    try:
        for tn in source_tile.neighbour_tiles:
            cur_tile = game_rules.board[tn]
            if cur_tile.tile_type == BoardTileType.LAND:
                if tn not in candidates:
                    candidates.append(tn)
            elif cur_tile.tile_type == BoardTileType.SEA \
                    and tn not in visited_seas \
                    and tn in game_state.armies \
                    and len(game_state.armies[str(tn)]) \
                    and game_state.armies[str(tn)][0].house == house_type:
                visited_seas.append(tn)
                find_reachable_targets(game_rules, game_state, house_type, game_rules[tn], candidates, visited_seas)
        return candidates
    except Exception as e:
        print_file_lineno_error(e)
