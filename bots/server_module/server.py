from server_module.add_order_reaction import AddOrderReaction
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from typing import NamedTuple, Optional


class GameHandle(NamedTuple):
    worker: str
    state: Optional[GameState]
    houses: set[HouseType]


class Server:
    def __init__(self):
        self.games: dict[str, GameHandle] = {}
        self.game_rules = Optional[GameRules]

    def add_game(self, game_id: str, worker: str):
        if game_id not in self.games:
            self.games[game_id] = GameHandle(worker, None, set())

    def add_game_rules(self, game_rules: GameRules):
        if self.game_rules is None:
            self.game_rules = game_rules

    def play_as(self, game_id: str, house_type: HouseType) -> bool:
        if game_id in self.games:
            if house_type in self.games[game_id].houses:
                return False
            else:
                self.games[game_id].houses.add(house_type)
                return True
        else:
            raise Exception('No game id ' + game_id + ' found')

    def add_state(self, game_id: str, game_state: GameState):
        if game_id in self.games:
            self.games[game_id] = GameHandle(
                self.games[game_id].worker,
                GameState.from_json(game_state),
                self.games[game_id].houses
            )

    def react(self, game_id: str, phase):
        if game_id in self.games:
            if phase['subPhase'] == 'addOrder':
                for h in phase['houseTypes']:
                    if h in self.games[game_id].houses:
                        reaction_handler = AddOrderReaction(HouseType[h.upper()], self.games[game_id].state)
                        reaction_handler.react()

