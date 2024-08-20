from server_module.game_rules.board_cards import BoardCards
from server_module.game_rules.board_tile import BoardTile
from server_module.game_rules.military_unit_info import MilitaryUnitInfo
import server_module.game_state.house_card as house_card
from server_module.game_state.military_unit_type import MilitaryUnitType
from server_module.game_state.order import Order
from server_module.game_state.order_type import OrderType


class GameRules:
    def __init__(
            self,
            board: list[BoardTile],
            kings_court_stars: list[int],
            supply_usage: list[list[int]],
            max_armies: dict[MilitaryUnitType, int],
            board_cards: BoardCards,
            loaded_orders: dict[OrderType, list[Order]],
            house_cards,  # type: list[house_card.HouseCard]
            military_units: dict[MilitaryUnitType, MilitaryUnitInfo]
    ):
        self.board = board
        self.kings_court_stars = kings_court_stars
        self.supply_usage = supply_usage
        self.max_armies = max_armies
        self.board_cards = board_cards
        self.loaded_orders = loaded_orders
        self.house_cards = house_cards
        self.military_units = military_units

    @classmethod
    def from_json(cls, json):
        return cls(
            list(BoardTile.from_json(bt) for bt in json['board']),
            json['kingsCourtStars'],
            json['supplyUsage'],
            {MilitaryUnitType.from_str(mut): num for mut, num in json['maxArmies'].items()},
            BoardCards.from_json(json['boardCards']),
            {OrderType.from_str(ot): list(Order.from_json(o) for o in od) for ot, od in json['loadedOrders'].items()},
            list(house_card.HouseCard.from_json(hc) for hc in json['houseCards']),
            {MilitaryUnitType.from_str(mut): MilitaryUnitInfo.from_json(mui) for mut, mui in json['militaryUnits'].items()}
        )

    def get_total_num_stars_by_court_position(self, court_position: int) -> int:
        try:
            return self.kings_court_stars[court_position]
        except IndexError:
            return 0
