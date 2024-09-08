from typing import NamedTuple, Optional

from base_service import BaseService
from server_module.game_rules.game_rules import GameRules
from server_module.game_state.armies import Armies
from server_module.game_state.available_orders import AvailableOrders
from server_module.game_state.bids import Bids
from server_module.game_state.combat import Combat
from server_module.game_state.discarded_house_cards import DiscardedHouseCards
from server_module.game_state.game_state import GameState
from server_module.game_state.house_card import HouseCard
from server_module.game_state.house_type import HouseType
from server_module.game_state.placed_orders import PlacedOrders
from server_module.game_state.power_tokens import PowerTokens
from server_module.game_state.supplies import Supplies
from server_module.game_state.tides_of_battle_card import TidesOfBattleCard
from server_module.game_state.tracks import Tracks


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

    def add_game_rules_and_state(self, game_rules: dict, game_id: str, game_state: dict[str, dict | int | bool]):
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

    def update_game_state(self, game_id: str,  game_state: dict[str, dict | int | bool]):
        state = self.games[game_id].state
        for part_name, value in game_state.items():
            if part_name == 'armies':
                state.armies = Armies(**game_state['armies'])
            elif part_name == 'placedOrders':
                state.placed_orders = PlacedOrders(**game_state['placedOrders']) if 'placedOrders' in game_state else PlacedOrders()
            elif part_name == 'tracks':
                state.tracks = Tracks(**game_state['tracks'])
            elif part_name == 'supplies':
                state.supplies = Supplies(**game_state['supplies'])
            elif part_name == 'discardedHouseCards':
                state.discarded_house_cards = DiscardedHouseCards(**game_state['discardedHouseCards'])
            elif part_name == 'powerTokens':
                state.power_tokens = PowerTokens(**game_state['powerTokens'])
            elif part_name == 'dominanceTokensUsage':
                state.dominance_tokens_usage = game_state['dominanceTokensUsage']
            elif part_name == 'availableOrders':
                state.available_orders = AvailableOrders(**game_state['availableOrders'])
            elif part_name == 'bids':
                state.bids = Bids(**game_state['bids']) if ('bids' in game_state and game_state['bids'] is not None) else None
            elif part_name == 'combat':
                state.combat = Combat.from_json(game_state['combat']) if ('combat' in game_state and game_state['combat'] is not None) else None
            elif part_name == 'wildlingCounter':
                state.wildling_counter = game_state['wildlingCounter']
            elif part_name == 'roundCounter':
                state.round_counter = game_state['roundCounter']
            elif part_name == 'subPhase':
                pass
            elif part_name == 'usedMusteringPoints':
                pass
            elif part_name == 'wildlingsStartedFrom12Points':
                pass
            else:
                raise Exception("Unknown game state part '{}'".format(part_name))


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
