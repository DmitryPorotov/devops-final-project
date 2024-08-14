from typing import Optional

from server_module.game_state.house_type import HouseType
import server_module.game_rules.game_rules as gr

class HouseCard:
    _game_rules = None  # type: Optional[gr.GameRules]

    @classmethod
    def set_game_rules(cls, game_rules) -> None:
        cls._game_rules = game_rules  # type: gr.GameRules

    def __init__(
            self,
            house: HouseType,
            code: int,
            name: str,
            strength: int,
            text: str = '',
            attack: int = 0,
            defense: int = 0
    ):
        self.defense = defense
        self.attack = attack
        self.text = text
        self.strength = strength
        self.name = name
        self.code = code
        self.house = house

    @classmethod
    def from_json(cls, json):
        return cls(
            HouseType[json['house'].upper()],
            json['code'],
            json['name'],
            json['strength'],
            json['text'],
            json['attack'],
            json['defense'],
        )

    @staticmethod
    def from_house_and_code(house: HouseType, code: int):
        if HouseCard._game_rules is None:
            raise RuntimeError('HouseCard\'s game rules are not initialized!')
        for house_card in HouseCard._game_rules.house_cards:
            if house_card.code == code and house_card.house == house:
                return house_card
