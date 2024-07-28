class MilitaryUnitInfo:
    def __init__(
            self,
            mustering_points: int,
            strength: int,
            can_retreat: bool,
    ):
        self.mustering_points = mustering_points
        self.strength = strength
        self.can_retreat = can_retreat

    @classmethod
    def from_json(cls, json):
        return cls(
            json['musteringPoints'],
            json['strength'],
            json['canRetreat'],
        )