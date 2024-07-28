from server_module.game_rules.round_event_card import RoundEventCard
from server_module.game_state.tides_of_battle_card import TidesOfBattleCard
from server_module.game_rules.wildling_card import WildlingCard


class BoardCards:
    def __init__(
            self,
            round_events1: list[RoundEventCard],
            round_events2: list[RoundEventCard],
            round_events3: list[RoundEventCard],
            wildlings: list[WildlingCard],
            tides_of_battle: list[TidesOfBattleCard]
    ):
        self.round_events1 = round_events1
        self.round_events2 = round_events2
        self.round_events3 = round_events3
        self.wildlings = wildlings
        self.tides_of_battle = tides_of_battle

    @classmethod
    def from_json(cls, json):
        return cls(
            list(RoundEventCard(**c) for c in json['deck1']),
            list(RoundEventCard(**c) for c in json['deck2']),
            list(RoundEventCard(**c) for c in json['deck3']),
            list(WildlingCard.from_json(c) for c in json['wildlings']),
            list(TidesOfBattleCard(**c) for c in json['tidesOfBattle'])
        )