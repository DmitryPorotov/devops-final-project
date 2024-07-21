from server.game_state.house_type import HouseType


class HouseCard:
    def __init__(self,
                 house: HouseType,
                 code: int,
                 name: str,
                 strength: int,
                 text: str = '',
                 attack: int = 0,
                 defence: int = 0
                 ):
        self.defence = defence
        self.attack = attack
        self.text = text
        self.strength = strength
        self.name = name
        self.code = code
        self.house = house
