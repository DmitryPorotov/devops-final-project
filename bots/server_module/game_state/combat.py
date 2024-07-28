from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.order import Order
from server_module.game_state.house_card import HouseCard, HouseType
from server_module.game_state.tides_of_battle_card import TidesOfBattleCard
from server_module.game_state.combat_outcome import CombatOutcome


type TileNumber = int


class Combat:
    def __init__(
            self,
            attacker_tile_num: TileNumber,
            attacker_house: HouseType,
            attacker_army: list[MilitaryUnit],
            attacker_order: Order,
            attacker_card: HouseCard,
            attacker_card_resolved: bool,
            attacker_tides_of_battle: TidesOfBattleCard,
            attacker_support: list[TileNumber],
            defender_tile_num: TileNumber,
            defender_house: HouseType,
            defender_army: list[MilitaryUnit],
            defender_order: Order,
            defender_card: HouseCard,
            defender_card_resolved: bool,
            defender_tides_of_battle: TidesOfBattleCard,
            defender_support: list[TileNumber],
            combat_outcome: CombatOutcome = None
    ):
        self.attacker_tile_num = attacker_tile_num
        self.attacker_house = attacker_house
        self.attacker_army = attacker_army
        self.attacker_order = attacker_order
        self.attacker_card = attacker_card
        self.attacker_card_resolved = attacker_card_resolved
        self.attacker_tides_of_battle = attacker_tides_of_battle
        self.attacker_support = attacker_support
        self.defender_tile_num = defender_tile_num
        self.defender_house = defender_house
        self.defender_army = defender_army
        self.defender_order = defender_order
        self.defender_card = defender_card
        self.defender_card_resolved = defender_card_resolved
        self.defender_tides_of_battle = defender_tides_of_battle
        self.defender_support = defender_support
        self.combat_outcome = combat_outcome

    @classmethod
    def from_json(cls, json):
        attack_army = (MilitaryUnit.from_json(json['attackerArmy']))
        return cls()
