from typing import TypedDict, TypeVar, Generic

from DTO.phases.all_phases import SubPhase

_T = TypeVar('_T')

class Reply(TypedDict, Generic[_T]):
    to: str | int  # * or t
    player_action: _T
    current_phase: SubPhase