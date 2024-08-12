from server_module.reactions.management.fill_with_bots import FillWithBots
from server_module.reactions.management.get_game_state import GetGameState
from server_module.reactions.game_phase_reactions.multi_house_reaction import MultiHouseReact
from server_module.reactions.management.join_game import JoinGame


def init():
    FillWithBots()
    GetGameState()
    JoinGame()
    MultiHouseReact.init()


def imports():
    for name, val in globals().items():
        try:
            if val.__module__.startswith("server_module"):
                yield val.__module__
        except AttributeError:
            pass
