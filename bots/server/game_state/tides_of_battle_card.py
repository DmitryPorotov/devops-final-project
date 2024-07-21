class TidesOfBattleCard:
    def __init__(
            self,
            code: int,
            power: int,
            death: bool = False,
            attack: bool = False,
            defence: bool = False
    ):
        self.code = code
        self.power = power
        self.death = death
        self.attack = attack
        self.defence = defence
