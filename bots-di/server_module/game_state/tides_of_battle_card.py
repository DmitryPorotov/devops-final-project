from typing import Optional


class TidesOfBattleCard:
    _game_rules = None  # type: Optional[GameRules]

    @classmethod
    def set_game_rules(cls, game_rules) -> None:
        cls._game_rules = game_rules  # type: GameRules

    def __init__(
            self,
            code: int,
            power: int,
            death: bool = False,
            attack: bool = False,
            defense: bool = False
    ):
        self.code = code
        self.power = power
        self.death = death
        self.attack = attack
        self.defense = defense

    @staticmethod
    def from_code(code: int):
        if TidesOfBattleCard._game_rules is None:
            raise RuntimeError('TidesOfBattleCard\'s game rules are not initialized!')
        for card in TidesOfBattleCard._game_rules.board_cards.tides_of_battle:
            if card.code == code:
                return card
