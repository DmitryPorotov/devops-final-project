from typing import Optional

from server_module.game_rules.game_rules import GameRules
from server_module.game_state.armies import Armies
from server_module.game_state.bids import Bids
from server_module.game_state.board_cards import BoardCards
from server_module.game_state.dominance_tokens_usage import DominanceTokensUsage
from server_module.game_state.placed_orders import PlacedOrders
from server_module.game_state.tracks import Tracks
from server_module.game_state.supplies import Supplies
from server_module.game_state.discarded_house_cards import DiscardedHouseCards
from server_module.game_state.power_tokens import PowerTokens
from server_module.game_state.used_mustering_points import UsedMusteringPoints
from server_module.game_state.available_orders import AvailableOrders
from server_module.game_state.combat import Combat


class GameState(dict[str, dict | int]):
    def __init__(
            self,
            armies: Armies,
            placed_orders: PlacedOrders,
            tracks: Tracks,
            supplies: Supplies,
            discarded_house_cards: DiscardedHouseCards,
            power_tokens: PowerTokens,
            dominance_tokens_usage: DominanceTokensUsage,
            used_mustering_points: UsedMusteringPoints,
            available_orders: AvailableOrders,
            bids: Optional[Bids],
            combat: Combat | None,
            wildling_counter: int = 6,
            round_counter: int = 1,
            **kwargs
    ):
        super().__init__(**kwargs)
        self['armies'] = self.armies = armies
        self['placedOrders'] = self.placed_orders = placed_orders
        self['tracks'] = self.tracks = tracks
        self['supplies'] =self.supplies = supplies
        self['discardedHouseCards'] = self.discarded_house_cards = discarded_house_cards
        self['powerTokens'] = self.power_tokens = power_tokens
        self['dominanceTokensUsage'] = self.dominance_tokens_usage = dominance_tokens_usage
        self['usedMusteringPoints'] = self.used_mustering_points = used_mustering_points
        self['availableOrders'] = self.available_orders = available_orders
        self['bids'] = self.bids = bids
        self['combat'] = self.combat = combat
        self['wildlingCounter'] = self.wildling_counter = wildling_counter
        self['roundCounter'] = self.round_counter = round_counter
        self['boardCards'] = self.board_cards = BoardCards()

    @classmethod
    def from_json(cls, json, game_rules: GameRules):
        placed_orders = PlacedOrders(**json['placedOrders']) if 'placedOrders' in json else PlacedOrders()
        available_orders = AvailableOrders.build_from_placed_orders(game_rules, placed_orders)
        return cls(
            Armies(**json['armies']),
            placed_orders,
            Tracks(**json['tracks']),
            Supplies(**json['supplies']),
            DiscardedHouseCards(**json['discardedHouseCards']),
            PowerTokens(**json['powerTokens']),
            DominanceTokensUsage(**json['dominanceTokensUsage']),
            UsedMusteringPoints(**json['usedMusteringPoints']),
            available_orders,
            Bids(**json['bids']) if ('bids' in json and json['bids'] is not None) else None,
            Combat.from_json(json['combat']) if ('combat' in json and json['combat'] is not None) else None,
            json['wildlingCounter'],
            json['roundCounter'],
        )

    def compare(self, other: "GameState") -> bool:
        for key, val in self.items():
            if isinstance(val, dict):
                if not val.compare(other[key]):
                    return False
            if isinstance(val, int):
                if other[key] != val:
                    return False
        return True

