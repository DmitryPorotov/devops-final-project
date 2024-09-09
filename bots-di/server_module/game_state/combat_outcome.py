from server_module.game_state.house_type import HouseType


class CombatOutcome(dict):
    def __init__(
            self,
            attacker_strength: int,
            defender_strength: int,
            attacker_units_to_kill: int,
            defender_units_to_kill: int,
            winner: HouseType,
            **kwargs,
    ):
        super().__init__(**kwargs)
        self.attacker_strength = attacker_strength
        self['attacker_strength'] = attacker_strength
        self.defender_strength = defender_strength
        self['defender_strength'] = defender_strength
        self.attacker_units_to_kill = attacker_units_to_kill
        self['attacker_units_to_kill'] = attacker_units_to_kill
        self.defender_units_to_kill = defender_units_to_kill
        self['defender_units_to_kill'] = defender_units_to_kill
        self.winner = winner
        self['winner'] = winner

    @classmethod
    def from_json(cls, json):
        if json is not None:
            return cls(
                json['attackerStrength'],
                json['defenderStrength'],
                json['attackerUnitsToKill'],
                json['defenderUnitsToKill'],
                json['winner'],
            )
        else:
            return None
        