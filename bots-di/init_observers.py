from server_module.reactions.game_action_reactions.action_react import ActionReact
from server_module.reactions.management.debugging_event_handler import DebuggingEventHandler
from server_module.reactions.management.error_retry_handler import ErrorRetryHandler
from server_module.reactions.management.fill_with_bots import FillWithBots
from server_module.reactions.management.get_game_state import GetGameState
from server_module.reactions.game_phase_reactions.phase_react import PhaseReact
from server_module.reactions.management.get_partial_game_state import GetPartialGameState
from server_module.reactions.management.join_game import JoinGame
from server_module.reactions.management.new_or_reset_game import NewOrResetGame
from server_module.reactions.management.round_events_main_phase_end import RoundEventsMainPhaseEnd

# note these imports are needed for DI - DO NOT REMOVE
from server_module.reactions.game_phase_reactions.round_events.tracks_bids_reaction import TracksBidsReaction
from server_module.reactions.game_phase_reactions.action.game_end_reaction import GameEndReaction
from server_module.reactions.game_action_reactions.action.clean_up_after_round_reaction import CleanUpAfterRoundReaction

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
    RoundEventsMainPhaseEnd()

def imports():
    for name, val in globals().items():
        try:
            if val.__module__.startswith("server_module"):
                yield val.__module__
        except AttributeError:
            pass
