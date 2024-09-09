from typing import Optional
import server_module.game_rules.game_rules as gr


class TidesOfBattleCard(dict):
    _game_rules = None  # type: Optional[gr.GameRules]

    @classmethod
    def set_game_rules(cls, game_rules) -> None:
        cls._game_rules = game_rules  # type: gr.GameRules

    def __init__(
            self,
            code: int,
            power: int,
            death: bool = False,
            attack: bool = False,
            defense: bool = False,
            **kwargs
    ):
        super().__init__(**kwargs)
        self.code = code
        self['code'] = code
        self.power = power
        self['power'] = power
        self.death = death
        self['death'] = death
        self.attack = attack
        self['attack'] = attack
        self.defense = defense
        self['defense'] = defense

    @staticmethod
    def from_code(code: int):
        if TidesOfBattleCard._game_rules is None:
            raise RuntimeError('TidesOfBattleCard\'s game rules are not initialized!')
        for card in TidesOfBattleCard._game_rules.board_cards.tides_of_battle:
            if card.code == code:
                return card
