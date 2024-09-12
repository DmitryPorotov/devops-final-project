from dependency_injector.wiring import Provide, inject

from containers_module import App
from events_service import EventSourcesService
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.phase_reactor import react_to_phase


class GetGameState:
    @inject
    def __init__(self, events_service: EventSourcesService = Provide[App.events], games_data: GamesDataService = Provide[App.game_service]):
        events_service.react_to_game_event_sources.message_get_game_state.subscribe(on_next=self.on_game_state)
        self._games_data = games_data

    def on_game_state(self, msg):
        self._games_data.add_game_rules_and_state(msg["gameRules"], msg["gameId"], msg["gameState"])
        id_ = msg["gameId"]
        sp = msg["gameState"]["subPhase"]
        react_to_phase(id_, sp)
