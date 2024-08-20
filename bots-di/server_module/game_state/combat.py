from typing import Optional

from server_module.game_state.military_unit import MilitaryUnit
from server_module.game_state.order import Order
from server_module.game_state.house_card import HouseCard, HouseType
from server_module.game_state.tides_of_battle_card import TidesOfBattleCard
from server_module.game_state.combat_outcome import CombatOutcome


class Combat:
    def __init__(
            self,
            attacker_tile_num: int,
            attacker_house: HouseType,
            attacker_army: list[MilitaryUnit],
            attacker_order: Order,
            attacker_card: HouseCard,
            attacker_tides_of_battle: TidesOfBattleCard,
            attacker_support: list[int],
            defender_tile_num: int,
            defender_house: HouseType,
            defender_army: list[MilitaryUnit],
            defender_order: Order,
            defender_card: HouseCard,
            defender_tides_of_battle: TidesOfBattleCard,
            defender_support: list[int],
            combat_outcome: Optional[CombatOutcome] = None
    ):
        self.attacker_tile_num = attacker_tile_num
        self.attacker_house = attacker_house
        self.attacker_army = attacker_army
        self.attacker_order = attacker_order
        self.attacker_card = attacker_card
        self.attacker_tides_of_battle = attacker_tides_of_battle
        self.attacker_support = attacker_support
        self.defender_tile_num = defender_tile_num
        self.defender_house = defender_house
        self.defender_army = defender_army
        self.defender_order = defender_order
        self.defender_card = defender_card
        self.defender_tides_of_battle = defender_tides_of_battle
        self.defender_support = defender_support
        self.combat_outcome = combat_outcome

    @classmethod
    def from_json(cls, json):
        attacker_house = HouseType[json['attackerHouse'].upper()]
        defender_house = HouseType[json['defenderHouse'].upper()]
        return cls(
            json['attackerTileNum'],
            attacker_house,
            [*(MilitaryUnit.from_json(mu) for mu in json['attackerArmy'])],
            Order.from_json(json['attackerOrder']),
            (HouseCard.from_house_and_code(attacker_house, json['attackerCard']) if 'attackerCard' in json else None),
            (TidesOfBattleCard.from_code(json['attackerTidesOfBattle']) if 'attackerTidesOfBattle' in json else None),
            (json['attackerSupport'] if 'attackerSupport' in json else None),  # array of tile numbers
            json['defenderTileNum'],
            defender_house,
            [*(MilitaryUnit.from_json(mu) for mu in json['defenderArmy'])],
            (Order.from_json(json['defenderOrder']) if 'defenderOrder' in json else None),
            (HouseCard.from_house_and_code(defender_house, json['defenderCard']) if 'defenderCard' in json else None),
            (TidesOfBattleCard.from_code(json['defenderTidesOfBattle']) if 'defenderTidesOfBattle' in json else None),
            (json['defenderSupport'] if 'defenderSupport' in json else None),
            (CombatOutcome.from_json(json['combatOutcome']) if 'combatOutcome' in json else None),
        )

