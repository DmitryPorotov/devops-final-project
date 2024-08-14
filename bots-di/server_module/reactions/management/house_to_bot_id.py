from enum import IntEnum

from server_module.game_state.house_type import HouseType


class HouseToBotId(IntEnum):
    WOLF = -1
    MOOSE = -2
    PUFFERFISH = -3
    KRAKEN = -4
    ROSE = -5
    LION = -6

    @staticmethod
    def get_bot_id_by_house(house: str | HouseType) -> int:
        return HouseToBotId[house.upper()].value
    