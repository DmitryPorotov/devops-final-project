from DTO.phases.all_phases import SubPhase
from typing import Callable, Optional

from server_module.game_state.house_type import HouseType


class MultiHouseReaction:
    def __init__(self):
        self._phase: Optional[SubPhase] = None
        self._houseMap = HouseTypesMap()

    def react(self, phase: SubPhase, house: HouseType , handler: Callable[[], None]):
        if self._phase is None:
            self._phase = phase
        if not ((self._phase['subPhase'] == phase['subPhase'] and 'trackType' not in self._phase and 'trackType' not in phase)
            or (self._phase['subPhase'] == phase['subPhase'] and 'trackType' in self._phase and 'trackType' in phase and self._phase['trackType'] == phase['trackType'])):
            self._phase = phase
            self._houseMap = HouseTypesMap()

        if not self._houseMap[house]:
            handler()
            self._houseMap[house] = True

class HouseTypesMap(dict):
    def __init__(self):
        super().__init__()
        self[HouseType.LION] = False
        self[HouseType.ROSE] = False
        self[HouseType.WOLF] = False
        self[HouseType.MOOSE] = False
        self[HouseType.KRAKEN] = False
        self[HouseType.PUFFERFISH] = False