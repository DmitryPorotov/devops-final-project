from server_module.reactions.management.fill_with_bots import FillWithBots
from server_module.reactions.management.get_game_state import GetGameState


def init():
    FillWithBots()
    GetGameState()


def imports():
    for name, val in globals().items():
        try:
            if val.__module__.startswith("server_module"):
                yield val.__module__
        except AttributeError:
            pass