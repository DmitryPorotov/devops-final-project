from typing import TypedDict, TypeVar, Generic, NotRequired

from DTO.phases.all_phases import SubPhase
from server_module.game_state.combat import Combat

_T = TypeVar('_T')


class Reply(TypedDict, Generic[_T]):
    to: str | int  # * or user_id
    player_action: _T
    current_phase: SubPhase
    combat: NotRequired[Combat]