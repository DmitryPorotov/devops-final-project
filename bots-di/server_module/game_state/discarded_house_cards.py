from server_module.game_state.house_card import HouseCard
from server_module.game_state.house_type import HouseType
from server_module.game_state.state_discrepancy_exception import StateDiscrepancyException


class DiscardedHouseCards(dict[HouseType, list[int]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for ht in kwargs:
                self[HouseType[ht.upper()]] = kwargs[ht]

    def discard_card(self, card: HouseCard):
        if card.house not in self:
            self[card.house] = []
        self[card.house].append(card.code)
        if len(self[card.house]) >= 7:
            self[card.house] = [card.code]

    def compare(self, other: "DiscardedHouseCards") -> bool:
        for ht, dhc in self.items():
            if ht not in other and len(dhc):
                raise StateDiscrepancyException("DiscardedHouseCards House {} is in local and not in other".format(ht))
            local = sorted(dhc)
            others = sorted(other[ht])
            if len(local) != len(others):
                raise StateDiscrepancyException("DiscardedHouseCards Number of local discarded house {} cards is not equal other".format(ht))
            for i in range(len(local)):
                if local[i] != others[i]:
                    raise StateDiscrepancyException("DiscardedHouseCards Local discarded house {} cards are not equal other".format(ht))
        for ht, dhc in other.items():
            if ht not in self and len(dhc):
                raise StateDiscrepancyException("DiscardedHouseCards House {} is in other and not in local".format(ht))
        return True