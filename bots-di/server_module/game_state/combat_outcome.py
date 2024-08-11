from server_module.game_state.house_type import HouseType


class CombatOutcome:
    def __init__(
            self,
            attacker_strength: int,
            defender_strength: int,
            attacker_units_to_kill: int,
            defender_units_to_kill: int,
            winner: HouseType,
    ):
        self.attacker_strength = attacker_strength
        self.defender_strength = defender_strength
        self.attacker_units_to_kill = attacker_units_to_kill
        self.defender_units_to_kill = defender_units_to_kill
        self.winner = winner

