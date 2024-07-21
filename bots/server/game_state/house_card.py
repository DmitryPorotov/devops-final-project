from server.game_state.house_type import HouseType


class HouseCard:
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