from DTO.phases.all_phases import SubPhase
from typing import Callable, Optional

from server_module.game_state.house_type import HouseType


class MultiHouseReaction:
    def __init__(self, house_map=None):
        self._phase: Optional[SubPhase] = None
        self._houseMap = HouseTypesMap() if house_map is None else house_map

    def react(self, phase: SubPhase, house: HouseType , handler: Callable[[], None]):
        if self._phase is None:
            self._phase = phase
        if not ((self._phase['subPhase'] == phase['subPhase'] and 'trackType' not in self._phase and 'trackType' not in phase)
            or (self._phase['subPhase'] == phase['subPhase'] and 'trackType' in self._phase and 'trackType' in phase and self._phase['trackType'] == phase['trackType'])):
            self._phase = phase
            self._houseMap = HouseTypesMap()

        if self._houseMap[house]:
            handler()
            self._houseMap[house] -= 1

    def set_house_map(self, house_map: dict[str, int]):
        self._houseMap = HouseTypesMap(**house_map)

class HouseTypesMap(dict):
    def __init__(self, **kwargs):
        super().__init__()
        if kwargs:
            for k, v in kwargs.items():
                self[HouseType[k.upper()]] = v
        else:
            self[HouseType.LION] = 1
            self[HouseType.ROSE] = 1
            self[HouseType.WOLF] = 1
            self[HouseType.MOOSE] = 1
            self[HouseType.KRAKEN] = 1
            self[HouseType.PUFFERFISH] = 1