from server_module.game_state.house_card import HouseCard
from server_module.game_state.house_type import HouseType


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