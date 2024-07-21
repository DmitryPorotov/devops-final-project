from game_state.game_state import GameState
from game_state.house_type import HouseType


class Server:
    def __init__(self):
        self.games: dict[str, (GameState, list[HouseType])] = {}

    def add_game(self, game_id: str, state: GameState):
        self.games[game_id] = (state,)

    def play_as(self, game_id: str, house_type: HouseType):
        if game_id in self.games:
            self.games[game_id] = (
                self.games[game_id][0],
                self.games[game_id][1].append(house_type) if self.games[game_id][1] else [house_type]
            )
        else:
            raise Exception('No game id ' + game_id + ' found')

    def react(self, game_id: str, house_type: HouseType, phase):
        if self.games[game_id][1] and house_type in self.games[game_id][1]:
            pass
