from server_module.game_state.house_type import HouseType
from server_module.game_state.track_type import TrackType


class Tracks(dict[TrackType, list[HouseType]]):
    def __init__(self, **kwargs):
        if kwargs is None:
            super().__init__()
        else:
            for tt in kwargs:
                track = []
                for i in range(len(kwargs[tt])):
                    track.append(HouseType[kwargs[tt][i].upper()])
                self[TrackType[tt.upper()]] = track
