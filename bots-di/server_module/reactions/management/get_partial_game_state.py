from dependency_injector.wiring import Provide, inject

from containers_module import App
from events_service import EventSourcesService
from server_module.games_data_service import GamesDataService
from server_module.reactions.game_phase_reactions.phase_react import PhaseReact
from server_module.reactions.game_phase_reactions.phase_reactor import react_to_phase


class GetPartialGameState:
    @inject
    def __init__(self, events_service: EventSourcesService = Provide[App.events], games_data: GamesDataService = Provide[App.game_manager]):
        events_service.react_to_game_event_sources.message_get_partial_game_state.subscribe(on_next=self.on_partial_game_state)
        self._games_data = games_data

    def on_partial_game_state(self, msg):
        self._games_data.update_game_state(msg["gameId"], msg['gameState'])
        if 'armies' in msg['gameState']:
            PhaseReact.multi_house_reaction.set_house_map({
                "moose": 1,
                 "kraken": 1,
                "lion": 1,
                "rose": 1,
                "wolf": 1,
                "pufferfish": 1,
            })
            react_to_phase(msg["gameId"], {
                "mainPhase": "phasePlanning",
                "subPhase": "addOrder",
                "houseTypes": [
                    "moose",
                    "kraken",
                    "lion",
                    "rose",
                    "wolf",
                    "pufferfish"
                 ]
            })