class BoardCards(dict[str, list]):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.round_events1: list[int] = []
        self['roundEvents1']: list[int] = self.round_events1
        self.round_events2: list[int] = []
        self['roundEvents2']: list[int] = self.round_events2
        self.round_events3: list[int] = []
        self['roundEvents3']: list[int] = self.round_events3
        self.wildlings: list[int] = []
        self['wildlings']: list[int] = self.wildlings
        self.tides_of_battle: list[int] = []
        self['tidesOfBattle']: list[int] = self.tides_of_battle


    def add_round_events1_card(self, cc):
        self.round_events1.insert(0, cc)

    def add_round_events2_card(self, cc):
        self.round_events2.insert(0, cc)

    def add_round_events3_card(self, cc):
        self.round_events3.insert(0, cc)

    def add_wildling_card(self, cc):
        self.wildlings.insert(0, cc)

    def add_tides_of_battle_card(self, cc):
        self.tides_of_battle.insert(0, cc)

    def compare(self, other):
        return True