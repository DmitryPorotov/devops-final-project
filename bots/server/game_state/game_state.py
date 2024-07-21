from server.game_state.armies import Armies
from server.game_state.placed_orders import PlacedOrders
from server.game_state.tracks import Tracks
from server.game_state.supplies import Supplies
from server.game_state.discarded_house_cards import DiscardedHouseCards
from server.game_state.power_tokens import PowerTokens
from server.game_state.used_mustering_points import UsedMusteringPoints
from server.game_state.available_orders import AvailableOrders
from server.game_state.combat import Combat


class GameState:
    def __init__(
            self,
            armies: Armies,
            placed_orders: PlacedOrders,
            tracks: Tracks,
            supplies: Supplies,
            discarded_house_cards: DiscardedHouseCards,
            power_tokens: PowerTokens,
            used_mustering_points: UsedMusteringPoints,
            available_orders: AvailableOrders,
            combat: Combat,
            wildling_counter: int = 6,
            round_counter: int = 1
    ):
        self.armies = armies 
        self.placed_orders = placed_orders 
        self.tracks = tracks 
        self.supplies = supplies 
        self.discarded_house_cards = discarded_house_cards 
        self.power_tokens = power_tokens
        self.used_mustering_points = used_mustering_points 
        self.available_orders = available_orders
        self.combat = combat 
        self.wildling_counter = wildling_counter
        self.round_counter = round_counter

    @classmethod
    def from_json(cls, json):
        return cls(
            Armies(**json['armies']),
            PlacedOrders(**json['placedOrders']) if 'placedOrders' in json else PlacedOrders(),
            Tracks(**json['tracks']),
            Supplies(**json['supplies']),
            DiscardedHouseCards(**json['discardedHouseCards']),
            PowerTokens(**json['powerTokens']),
            UsedMusteringPoints(**json['usedMusteringPoints']),
            AvailableOrders(**json['availableOrders']) if 'availableOrders' in json else AvailableOrders(),
            Combat.from_json(json['combat']) if ('combat' in json and json['combat'] is not None) else None,
            json['wildlingCounter'],
            json['roundCounter'],
        )