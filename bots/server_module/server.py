from handle_request_for_bots import HouseToBotId
from server_module.add_order_reaction import AddOrderReaction
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.game_state import GameState
from server_module.game_state.house_type import HouseType
from typing import NamedTuple, Optional

from server_module.game_state.track_type import TrackType


class GameHandle(NamedTuple):
    worker: str
    state: Optional[GameState]
    houses: set[HouseType]


class Server:
    def __init__(self):
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
            raise Exception('No game id ' + game_id + ' found')

    def react(self, game_id: str, phase):
        if game_id in self.games:
            if phase['subPhase'] == 'addOrder':
                for h in phase['houseTypes']:
                    if h in self.games[game_id].houses:
                        bot_id = HouseToBotId[h.upper()].value
                        total_stars = self.game_rules.get_total_num_stars_by_court_position(
                            self.games[game_id].state.tracks[TrackType('court')].index(h)
                        )
                        reaction_handler = AddOrderReaction(h, self.games[game_id].state, total_stars)
                        moves = reaction_handler.get_moves()
                        json_like_arr = reaction_handler.to_json(bot_id, game_id, moves)
                        for m in json_like_arr:
                            yield m
                        yield {
                            'userId': bot_id,
                            'gameId': game_id,
                            'action': 'game_action',
                            'player_action': {
                                'actionType': 'openOrders',
                                'houseType': h,
                            }
                        }


