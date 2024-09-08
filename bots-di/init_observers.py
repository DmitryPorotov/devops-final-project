from server_module.reactions.game_action_reactions.action_react import ActionReact
from server_module.reactions.management.debugging_event_handler import DebuggingEventHandler
from server_module.reactions.management.error_retry_handler import ErrorRetryHandler
from server_module.reactions.management.fill_with_bots import FillWithBots
from server_module.reactions.management.get_game_state import GetGameState
from server_module.reactions.game_phase_reactions.phase_react import PhaseReact
from server_module.reactions.management.get_partial_game_state import GetPartialGameState
from server_module.reactions.management.join_game import JoinGame
from server_module.reactions.management.new_or_reset_game import NewOrResetGame


def init():
    FillWithBots()
    GetGameState()
    JoinGame()
    NewOrResetGame()
    PhaseReact.init()
    ActionReact.init()
    ErrorRetryHandler()
    GetPartialGameState()
    DebuggingEventHandler()


def imports():
    for name, val in globals().items():
        try:
            if val.__module__.startswith("server_module"):
                yield val.__module__
        except AttributeError:
            pass
