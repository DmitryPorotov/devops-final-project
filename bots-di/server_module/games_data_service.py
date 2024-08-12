from typing import NamedTuple, Optional

from base_service import BaseService
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_card import HouseCard
from server_module.game_state.house_type import HouseType
from server_module.game_state.tides_of_battle_card import TidesOfBattleCard


class GameHandle(NamedTuple):
    worker: str
    state: Optional[GameState]
    houses: set[HouseType]


class GamesDataService(BaseService):
    def __init__(self):
        super().__init__()
        self.games: dict[str, GameHandle] = {}
        self.game_rules: Optional[GameRules] = None

    def add_game(self, game_id: str, worker: str):
        if game_id not in self.games:
            self.games[game_id] = GameHandle(worker, None, set())

    def delete_game(self, game_id: str):
        if game_id in self.games:
            del self.games[game_id]

    def add_game_rules_and_state(self, game_rules: dict, game_id: str,  game_state: dict):
        if self.game_rules is None:
            self.game_rules = GameRules.from_json(game_rules)
            HouseCard.set_game_rules(self.game_rules)
            TidesOfBattleCard.set_game_rules(self.game_rules)
        if game_id in self.games:
            self.games[game_id] = GameHandle(
                self.games[game_id].worker,
                GameState.from_json(game_state, self.game_rules),
                self.games[game_id].houses
            )

    def play_as(self, game_id: str, house_type: HouseType) -> bool:
        if game_id in self.games:
            if house_type in self.games[game_id].houses:
                return False
            else:
                self.games[game_id].houses.add(house_type)
                return True
        else:
            self.logger.debug('No game id ' + game_id + ' found')
            raise Exception('No game id ' + game_id + ' found')

    def get_game(self, game_id: str) -> Optional[GameHandle]:
        try:
            return self.games[game_id]
        except KeyError:
            return None
